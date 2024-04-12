package io.zibi.komar.broker

import io.zibi.komar.BrokerConstants.INTERCEPT_HANDLER_PROPERTY_NAME
import io.zibi.komar.BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME
import io.zibi.komar.broker.config.FileResourceLoader
import io.zibi.komar.broker.config.IConfig
import io.zibi.komar.broker.config.IResourceLoader
import io.zibi.komar.broker.config.MemoryConfig
import io.zibi.komar.broker.config.ResourceLoaderConfig
import io.zibi.komar.broker.security.IAuthenticator
import io.zibi.komar.broker.security.IAuthorizatorPolicy
import io.zibi.komar.broker.subscriptions.CTrieSubscriptionDirectory
import io.zibi.komar.broker.subscriptions.ISubscriptionsDirectory
import io.zibi.komar.interception.BrokerInterceptor
import io.zibi.komar.interception.InterceptHandler
import io.zibi.komar.logging.LoggingUtils.getInterceptorIds
import io.zibi.komar.persistence.H2Builder
import io.zibi.komar.persistence.MemorySubscriptionsRepository
import io.zibi.codec.mqtt.MqttPublishMessage
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.concurrent.Volatile
import io.zibi.komar.broker.security.Authenticator
import io.zibi.komar.broker.security.AuthorizatorPolicy
import io.zibi.codec.mqtt.reasoncode.Disconnect
import java.util.Properties

class Server {

    private var innerServer: InnerServer? = null

    /**
     * Starts Moquette bringing the configuration from the file located at m_config/zibi.komar.conf
     * @throws IOException in case of any IO error.
     */
    @Throws(IOException::class)
    fun startServer(configFile: File = defaultConfigFile()) {
        LOG.info(
            "Starting Moquette integration. Configuration file path: {}",
            configFile.absolutePath
        )
        val filesystemLoader: IResourceLoader = FileResourceLoader(configFile)
        val config: IConfig = ResourceLoaderConfig(filesystemLoader)
        innerServer = InnerServer(config)
    }

    @Throws(IOException::class)
    fun startServer(configProps: Properties) {
        LOG.debug("Starting Moquette integration using properties object")
        val config: IConfig = MemoryConfig(configProps)
        innerServer = InnerServer(config)
    }

    fun startServer(
        config: IConfig,
        handlers: List<InterceptHandler> = emptyList<InterceptHandler>(),
        authenticator: IAuthenticator = Authenticator.init(props = config),
        authorizatorPolicy: IAuthorizatorPolicy = AuthorizatorPolicy.init(props = config),
    ) {
        innerServer =
            InnerServer(config, handlers, authenticator, authorizatorPolicy)
    }


    suspend fun internalPublish(msg: MqttPublishMessage, clientId: String?) {
        innerServer?.internalPublish(msg, clientId)
    }

    fun stopServer() {
        innerServer?.stopServer()
        innerServer = null
    }

    val port: Int
        get() = innerServer?.port ?: -1
    val sslPort: Int
        get() = innerServer?.sslPort ?: -1

    /**
     * SPI method used by Broker embedded applications to add intercept handlers.
     *
     * @param interceptHandler the handler to add.
     */
    fun addInterceptHandler(interceptHandler: InterceptHandler) {
        innerServer?.addInterceptHandler(interceptHandler)
    }

    /**
     * SPI method used by Broker embedded applications to remove intercept handlers.
     *
     * @param interceptHandler the handler to remove.
     */
    fun removeInterceptHandler(interceptHandler: InterceptHandler) {
        innerServer?.removeInterceptHandler(interceptHandler)
    }

    /**
     * Return a list of descriptors of connected clients.
     */
    fun listConnectedClients(): Collection<ClientDescriptor> = innerServer?.listConnectedClients() ?: listOf()

    private inner class InnerServer(
        config: IConfig,
        handlers: List<InterceptHandler> = emptyList<InterceptHandler>(),
//        sslCtxCreator: ISslContextCreator = DefaultMoquetteSslContextCreator(props = config),
        authenticator: IAuthenticator = Authenticator.init(props = config),
        authorizatorPolicy: IAuthorizatorPolicy = AuthorizatorPolicy.init(props = config),
    ) {
        private val scheduler: ScheduledExecutorService
        private val acceptor: MQTTAcceptor
        private val dispatcher: PostOffice
        private val sessions: SessionRegistry
        private val interceptor: BrokerInterceptor

        @Volatile
        private var initialized = false
        private var h2Builder: H2Builder? = null


        init {
            val start = System.currentTimeMillis()
            LOG.trace(
                "Starting Moquette Server. MQTT message interceptors={}",
                getInterceptorIds(handlers)
            )
            scheduler = Executors.newScheduledThreadPool(1)
            System.getProperty(INTERCEPT_HANDLER_PROPERTY_NAME)?.let {
                config.setProperty(INTERCEPT_HANDLER_PROPERTY_NAME, it)
            }
            val persistencePath = config.getProperty(PERSISTENT_STORE_PROPERTY_NAME)
            LOG.debug("Configuring Using persistent store file, path: {}", persistencePath)
            interceptor = initInterceptors(config, handlers)
            LOG.debug("Initialized MQTT protocol processor")
            val subscriptionsRepository: ISubscriptionsRepository
            val queueRepository: IQueueRepository
            val retainedRepository: IRetainedRepository
            if (!persistencePath.isNullOrEmpty()) {
                LOG.trace("Configuring H2 subscriptions store to {}", persistencePath)
                H2Builder(config, scheduler).let {
                    subscriptionsRepository = it.subscriptionsRepository()
                    queueRepository = it.queueRepository()
                    retainedRepository = it.retainedRepository()
                    h2Builder = it
                }
            } else {
                LOG.trace("Configuring in-memory subscriptions store")
                subscriptionsRepository = MemorySubscriptionsRepository()
                queueRepository = MemoryQueueRepository()
                retainedRepository = MemoryRetainedRepository()
            }
            val subscriptions: ISubscriptionsDirectory =
                CTrieSubscriptionDirectory(subscriptionsRepository)
            val authorizator = Authorizator(authorizatorPolicy)
            sessions = SessionRegistry(subscriptions, queueRepository, authorizator)
            dispatcher =
                PostOffice(subscriptions, retainedRepository, sessions, interceptor, authorizator)
            val connectionFactory = MQTTConnectionFactory(
                brokerConfig = BrokerConfiguration(config), authenticator, sessions, dispatcher
            )
            acceptor = MQTTAcceptor(connectionFactory = connectionFactory, props = config)
            val startTime = System.currentTimeMillis() - start
            LOG.info("Moquette integration has been started successfully in {} ms", startTime)
            interceptor.notifyServerStarting("Server start")
            initialized = true
        }

        private fun initInterceptors(
            props: IConfig,
            embeddedObservers: List<InterceptHandler>
        ): BrokerInterceptor {
            LOG.info("Configuring message interceptors...")
            val observers: MutableList<InterceptHandler> = embeddedObservers.toMutableList()
            val interceptorClassName = props.getProperty(INTERCEPT_HANDLER_PROPERTY_NAME)
            if (!interceptorClassName.isNullOrEmpty()) {
                val handler = Utils.loadClass(
                    interceptorClassName, InterceptHandler::class.java,
                    Server::class.java, this@Server
                )
                if (handler != null) {
                    observers.add(handler)
                }
            }
            return BrokerInterceptor( observers)
        }

        /**
         * Use the broker to publish a message. It's intended for embedding applications. It can be used
         * only after the integration is correctly started with startServer.
         *
         * @param msg      the message to forward. The ByteBuf in the message will be released.
         * @param clientId the id of the sending integration.
         * @throws IllegalStateException if the integration is not yet started
         */
        suspend fun internalPublish(msg: MqttPublishMessage, clientId: String?) {
            val messageID = msg.variableHeader().packetId
            if (!initialized) {
                LOG.error("Moquette is not started, internal message cannot be published. CId: {}, messageId: {}", clientId, messageID)
                throw IllegalStateException("Can't publish on a integration is not yet started")
            }
            LOG.trace("Internal publishing message CId: {}, messageId: {}", clientId, messageID)
            dispatcher.internalPublish(msg)
//            msg.payload().release() TODO ??
        }

        fun stopServer() {
            LOG.info("Unbinding integration from the configured ports")
            acceptor.close()
            LOG.trace("Stopping MQTT protocol processor")
            initialized = false

            // calling shutdown() does not actually stop tasks that are not cancelled,
            // and SessionsRepository does not stop its tasks. Thus shutdownNow().
            scheduler.shutdownNow()
            h2Builder?.let {
                LOG.trace("Shutting down H2 persistence {}", "")
                it.closeStore()
            }
            interceptor.notifyServerShuttingDown(Disconnect.SERVER_SHUTTING_DOWN.toDesc())
            interceptor.stop()
            LOG.info("Moquette integration has been stopped.")
        }

        val port: Int
            get() = acceptor.port
        val sslPort: Int
            get() = acceptor.sslPort
        /**
         * SPI method used by Broker embedded applications to get list of subscribers. Returns null if
         * the broker is not started.
         *
         * @return list of subscriptions.
         */
        // TODO reimplement this
        //    public List<Subscription> getSubscriptions() {
        //        if (m_processorBootstrapper == null) {
        //            return null;
        //        }
        //        return this.subscriptionsStore.listAllSubscriptions();
        //    }
        /**
         * SPI method used by Broker embedded applications to add intercept handlers.
         *
         * @param interceptHandler the handler to add.
         */
        fun addInterceptHandler(interceptHandler: InterceptHandler) {
            if (!initialized) {
                LOG.error(
                    "Moquette is not started, MQTT message interceptor cannot be added. InterceptorId={}",
                    interceptHandler.getID()
                )
                throw IllegalStateException("Can't register interceptors on a integration that is not yet started")
            }
            LOG.info("Adding MQTT message interceptor. InterceptorId={}", interceptHandler.getID())
            interceptor.addInterceptHandler(interceptHandler)
        }

        /**
         * SPI method used by Broker embedded applications to remove intercept handlers.
         *
         * @param interceptHandler the handler to remove.
         */
        fun removeInterceptHandler(interceptHandler: InterceptHandler) {
            if (!initialized) {
                LOG.error(
                    "Moquette is not started, MQTT message interceptor cannot be removed. InterceptorId={}",
                    interceptHandler.getID()
                )
                throw IllegalStateException("Can't deregister interceptors from a integration that is not yet started")
            }
            LOG.info(
                "Removing MQTT message interceptor. InterceptorId={}",
                interceptHandler.getID()
            )
            interceptor.removeInterceptHandler(interceptHandler)
        }

        /**
         * Return a list of descriptors of connected clients.
         */
        fun listConnectedClients(): Collection<ClientDescriptor> {
            return sessions.listConnectedClients()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Server::class.java)

        private fun defaultConfigFile(): File {
            val configPath = System.getProperty("zibi.komar.path", null)
            return File(configPath, IConfig.DEFAULT_CONFIG)
        }
    }
}