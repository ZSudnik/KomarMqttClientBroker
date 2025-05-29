package io.zibi.komar.broker

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.zibi.komar.BrokerConstants.DEFAULT_HOST_ADDRESS
import io.zibi.komar.BrokerConstants.DISABLED_PORT_BIND
import io.zibi.komar.BrokerConstants.PORT_PROPERTY_NAME
import io.zibi.komar.broker.config.IConfig
import io.zibi.codec.mqtt.MqttConstant
import io.zibi.komar.BrokerConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.net.BindException
import kotlin.system.exitProcess

internal class MQTTAcceptor(
    private val connectionFactory: MQTTConnectionFactory,
    props: IConfig,
) {

    private val ports: MutableMap<String, Int> = mutableMapOf()
    private var komarSoBacklog = 0
    private var komarSoReuseaddr = false
    private var komarTcpNodelay = false
    private var komarSoKeepalive = false
    private var useTasmota: Boolean = false
    private var komarChannelTimeoutSeconds = 0
    private var maxBytesInMessage = 0

    init{
        LOG.debug("Initializing Netty acceptor")
        komarSoBacklog = props.intProp(BrokerConstants.KOMAR_SO_BACKLOG_PROPERTY_NAME, 128)
        komarSoReuseaddr = props.boolProp(BrokerConstants.KOMAR_SO_REUSEADDR_PROPERTY_NAME, true)
        komarTcpNodelay = props.boolProp(BrokerConstants.KOMAR_TCP_NODELAY_PROPERTY_NAME, true)
        komarSoKeepalive = props.boolProp(BrokerConstants.KOMAR_SO_KEEPALIVE_PROPERTY_NAME, true)
        komarChannelTimeoutSeconds =
            props.intProp(BrokerConstants.KOMAR_CHANNEL_TIMEOUT_SECONDS_PROPERTY_NAME, 10)
        maxBytesInMessage = props.intProp(
            BrokerConstants.KOMAR_MAX_BYTES_IN_MESSAGE,
            MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE
        )
        useTasmota = props.boolProp(BrokerConstants.ALLOW_TASMOTA, false)
        initializePlainTCPTransport( props)
     }

    private var bindJob: Job? = null
    private var server: ServerSocket? = null

    fun close(){
        server?.close()
        bindJob?.cancel()
        MQTTHandler.serverShuttingDown()
    }

    private fun initFactory(
        host: String,
        port: Int,
        protocol: String,
    ) {
        LOG.debug("Initializing integration. Protocol={}", protocol)
        try {
            LOG.debug("Binding integration. host={}, port={}", host, port)
            // Bind and start to accept incoming connections.
            val selectorManager = ActorSelectorManager(Dispatchers.IO)
            server = aSocket(selectorManager).tcp().bind(host, port)
            bindJob = CoroutineScope(selectorManager.coroutineContext).launch {
                try {
                    while (true) {
                        val socket = server!!.accept()
                        MQTTHandler.addConnection(
                            connectionFactory,
                            socket,
                            useTasmota,
                            maxBytesInMessage
                        )
                    }
                }catch (e: Exception){
                    LOG.debug("Server is close")
                }
            }
        } catch (ex: Exception) {
            if (ex is BindException) {
                LOG.error("Cannot bind to port: $port", ex)
                exitProcess(1)
            } else {
                LOG.error(
                    "An interruptedException was caught while initializing integration. Protocol={}",
                    protocol,
                    ex
                )
                throw RuntimeException(ex)
            }
        }
    }

    val port: Int
        get() = ports.computeIfAbsent(PLAIN_MQTT_PROTO) { 0 }
    val sslPort: Int
        get() = ports.computeIfAbsent(SSL_MQTT_PROTO) { 0 }


    private fun initializePlainTCPTransport( props: IConfig) {
        LOG.debug("Configuring TCP MQTT transport")
        val host = props.getProperty(BrokerConstants.HOST_PROPERTY_NAME,DEFAULT_HOST_ADDRESS)
        val port = props.getProperty(PORT_PROPERTY_NAME, DISABLED_PORT_BIND)
        if (DISABLED_PORT_BIND == port) {
            LOG.info(
                "Property {} has been set to {}. TCP MQTT will be disabled",
                PORT_PROPERTY_NAME, DISABLED_PORT_BIND
            )
            return
        }
        initFactory(host, port.toInt(), PLAIN_MQTT_PROTO)
    }

    companion object {
        private const val MQTT_SUBPROTOCOL_CSV_LIST = "mqtt, mqttv3.1, mqttv3.1.1"
        const val PLAIN_MQTT_PROTO = "TCP MQTT"
        const val SSL_MQTT_PROTO = "SSL MQTT"
        private val LOG = LoggerFactory.getLogger(MQTTAcceptor::class.java)
    }
}

//   fun close() {
//        LOG.debug("Closing Netty acceptor...")
////        if (workerGroup == null || bossGroup == null) {
////            LOG.error("Netty acceptor is not initialized")
////            throw IllegalStateException("Invoked close on an Acceptor that wasn't initialized")
////        }
////        val workerWaiter = workerGroup!!.shutdownGracefully()
////        val bossWaiter = bossGroup!!.shutdownGracefully()
//
//        /*
//         * We shouldn't raise an IllegalStateException if we are interrupted. If we did so, the
//         * broker is not shut down properly.
//         */LOG.info("Waiting for worker and boss event loop groups to terminate...")
//        try {
////            workerWaiter.await(10, TimeUnit.SECONDS)
////            bossWaiter.await(10, TimeUnit.SECONDS)
//        } catch (iex: InterruptedException) {
//            LOG.warn("An InterruptedException was caught while waiting for event loops to terminate...")
//        }
////        if (!workerGroup!!.isTerminated) {
////            LOG.warn("Forcing shutdown of worker event loop...")
////            workerGroup!!.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS)
////        }
////        if (!bossGroup!!.isTerminated) {
////            LOG.warn("Forcing shutdown of boss event loop...")
////            bossGroup!!.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS)
////        }
//    }