package io.zibi.komar.interception

import io.zibi.komar.broker.subscriptions.Subscription
import io.zibi.komar.interception.messages.InterceptAcknowledgedMessage
import io.zibi.komar.interception.messages.InterceptConnectMessage
import io.zibi.komar.interception.messages.InterceptConnectionLostMessage
import io.zibi.komar.interception.messages.InterceptDisconnectMessage
import io.zibi.komar.interception.messages.InterceptPublishMessage
import io.zibi.komar.interception.messages.InterceptServerAppNotification
import io.zibi.komar.interception.messages.InterceptSubscribeMessage
import io.zibi.komar.interception.messages.InterceptUnsubscribeMessage
import io.zibi.komar.logging.LoggingUtils
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttPublishMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

/**
 * An interceptor that execute the interception tasks asynchronously.
 */
class BrokerInterceptor( handlers: List<InterceptHandler>) : Interceptor {

    private val handlers: MutableMap<Class<*>, MutableList<InterceptHandler>>
    private val serviceJob: Job = SupervisorJob()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + serviceJob)

    init {
        LOG.info(
            "Initializing broker interceptor. InterceptorIds={}",
            LoggingUtils.getInterceptorIds(handlers)
        )
        this.handlers = HashMap()
        for (messageType in InterceptHandler.ALL_MESSAGE_TYPES) {
            this.handlers[messageType] = CopyOnWriteArrayList()
        }
        for (handler in handlers) {
            addInterceptHandler(handler)
        }
    }

    /**
     * Shutdown graciously the executor service
     */
    fun stop() {
        LOG.info("Shutting down interceptor coroutine ...")
        CoroutineScope(Dispatchers.IO).launch {
            serviceJob.cancelAndJoin()
        }
        LOG.info("interceptors stopped")
    }

    override fun notifyClientConnected(msg: MqttConnectMessage) {
        handlers[InterceptConnectMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Sending MQTT CONNECT message to interceptor. CId={}, interceptorId={}",
                msg.payload().clientIdentifier(), handler.getID()
            )
            scope.launch { handler.onConnect(InterceptConnectMessage(msg)) }
        }
    }

    override fun notifyClientDisconnected(clientID: String, username: String) {
        handlers[InterceptDisconnectMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT client disconnection to interceptor. CId={}, username={}, interceptorId={}",
                clientID, username, handler.getID()
            )
            scope.launch {
                handler.onDisconnect(InterceptDisconnectMessage(clientID, username))
            }
        }
    }

    override fun notifyClientConnectionLost(clientID: String, username: String) {
        handlers[InterceptConnectionLostMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying unexpected MQTT client disconnection to interceptor CId={}, username={}, " +
                        "interceptorId={}", clientID, username, handler.getID()
            )
            scope.launch {
                handler.onConnectionLost(InterceptConnectionLostMessage(clientID, username))
            }
        }
    }

    override fun notifyServerShuttingDown(reason: String) {
        handlers[InterceptServerAppNotification::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT server shut down to interceptor reason={} ", reason
            )
            scope.launch {
                handler.onServerNotification(InterceptServerAppNotification(reason))
            }
        }
    }

    override fun notifyServerStarting(reason: String) {
        handlers[InterceptServerAppNotification::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT server starting to interceptor reason={} ", reason
            )
            scope.launch {
                handler.onServerNotification(InterceptServerAppNotification(reason))
            }
        }
    }
    override fun notifyTopicPublished(
        msg: MqttPublishMessage,
        clientID: String,
        username: String
    ) {
//        msg.retain() TODO
        scope.launch {
            try {
                val messageId = msg.variableHeader().packetId
                val topic = msg.variableHeader().topicName
                handlers[InterceptPublishMessage::class.java]?.forEach { handler ->
                    LOG.debug(
                        "Notifying MQTT PUBLISH message to interceptor. CId={}, messageId={}, topic={}, "
                                + "interceptorId={}", clientID, messageId, topic, handler.getID()
                    )
                    // Sending to the outside, make a retainedDuplicate.
//                    handler.onPublish(InterceptPublishMessage(msg.retainedDuplicate(), clientID, username))
                    handler.onPublish(InterceptPublishMessage(msg, clientID, username))
                }
            } finally {
//                ReferenceCountUtil.release(msg) TODO
            }
        }
    }

    override fun notifyTopicSubscribed(sub: Subscription, username: String) {
        handlers[InterceptSubscribeMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT SUBSCRIBE message to interceptor. CId={}, topicFilter={}, interceptorId={}",
                sub.clientId, sub.topicFilter, handler.getID()
            )
            scope.launch { handler.onSubscribe(InterceptSubscribeMessage(sub, username)) }
        }
    }

    override fun notifyTopicUnsubscribed(topic: String, clientID: String, username: String) {
        handlers[InterceptUnsubscribeMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT UNSUBSCRIBE message to interceptor. CId={}, topic={}, interceptorId={}",
                clientID, topic, handler.getID()
            )
            scope.launch {
                handler.onUnsubscribe(InterceptUnsubscribeMessage(topic, clientID, username))
            }
        }
    }

    override fun notifyMessageAcknowledged(msg: InterceptAcknowledgedMessage) {
        handlers[InterceptAcknowledgedMessage::class.java]?.forEach { handler ->
            LOG.debug(
                "Notifying MQTT ACK message to interceptor. CId={}, messageId={}, topic={}, interceptorId={}",
                msg.msg, msg.packetID, msg.topic, handler.getID()
            )
            scope.launch { handler.onMessageAcknowledged(msg) }
        }
    }

    override fun addInterceptHandler(interceptHandler: InterceptHandler) {
        val interceptedMessageTypes = getInterceptedMessageTypes(interceptHandler)
        LOG.info(
            "Adding MQTT message interceptor. InterceptorId={}, handledMessageTypes={}",
            interceptHandler.getID(), interceptedMessageTypes
        )
        for (interceptMessageType in interceptedMessageTypes) {
            if( !handlers.contains(interceptMessageType)) handlers[interceptMessageType] = mutableListOf()
            handlers[interceptMessageType]?.add(interceptHandler)
        }
    }

    override fun removeInterceptHandler(interceptHandler: InterceptHandler) {
        val interceptedMessageTypes = getInterceptedMessageTypes(interceptHandler)
        LOG.info(
            "Removing MQTT message interceptor. InterceptorId={}, handledMessageTypes={}",
            interceptHandler.getID(), interceptedMessageTypes
        )
        for (interceptMessageType in interceptedMessageTypes) {
            handlers[interceptMessageType]?.remove(interceptHandler)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(BrokerInterceptor::class.java)
        private fun getInterceptedMessageTypes(interceptHandler: InterceptHandler): Array<Class<*>> {
            return interceptHandler.getInterceptedMessageTypes()
                //?: return InterceptHandler.ALL_MESSAGE_TYPES
        }
    }
}
