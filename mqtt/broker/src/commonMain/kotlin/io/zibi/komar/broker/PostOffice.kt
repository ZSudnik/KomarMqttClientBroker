package io.zibi.komar.broker

import io.zibi.komar.broker.Session.Will
import io.zibi.komar.broker.Utils.messageId
import io.zibi.komar.broker.subscriptions.ISubscriptionsDirectory
import io.zibi.komar.broker.subscriptions.Subscription
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.komar.interception.BrokerInterceptor
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttFixedHeader
import io.zibi.codec.mqtt.MqttMessageVariableHeader
import io.zibi.codec.mqtt.MqttMessageType
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttSubAckMessage
import io.zibi.codec.mqtt.MqttSubAckPayload
import io.zibi.codec.mqtt.MqttSubscribeMessage
import io.zibi.codec.mqtt.MqttTopicSubscription
import org.slf4j.LoggerFactory

class PostOffice(
    private val subscriptions: ISubscriptionsDirectory,
    private val retainedRepository: IRetainedRepository,
    private var sessionRegistry: SessionRegistry,
    private val interceptor: BrokerInterceptor,
    private val authorizator: Authorizator
) {
    fun init(sessionRegistry: SessionRegistry) {
        this.sessionRegistry = sessionRegistry
    }

    suspend fun fireWill(will: Will) {
        // MQTT 3.1.2.8-17
        publish2Subscribers(will.payload, Topic(will.topic), will.qos)
    }

    suspend fun subscribeClientToTopics(
        msg: MqttSubscribeMessage, clientID: String, username: String, mqttConnection: MQTTConnection
    ) {
        // verify which topics of the subscribe ongoing has read access permission
        val messageID = messageId(msg)
        val ackTopics = authorizator.verifyTopicsReadAccess(clientID, username, msg)
        val ackMessage = doAckMessageFromValidateFilters(ackTopics, messageID)

        // store topics subscriptions in session
        val newSubscriptions = ackTopics
            .filter { req: MqttTopicSubscription -> req.qualityOfService() != MqttQoS.FAILURE }
            .map { req: MqttTopicSubscription ->
                val topic = Topic(req.topicFilter)
                Subscription(clientID, topic, req.qualityOfService())
            }
        newSubscriptions.forEach {
            subscriptions.add(it)
        }

        // add the subscriptions to Session
        val session = sessionRegistry.retrieve(clientID)
        session?.subscriptions?.addAll(newSubscriptions)

        // send ack message
        mqttConnection.sendSubAckMessage(messageID, ackMessage)
        publishRetainedMessagesForSubscriptions(clientID, newSubscriptions)
        newSubscriptions.forEach {
            interceptor.notifyTopicSubscribed(it, username)
        }
    }

    private suspend fun publishRetainedMessagesForSubscriptions(
        clientID: String,
        newSubscriptions: List<Subscription>
    ) {
        val targetSession = sessionRegistry.retrieve(clientID)
        for (subscription in newSubscriptions) {
            val topicFilter = subscription.topicFilter.toString()
            val retainedMsgs = retainedRepository.retainedOnTopic(topicFilter)
            if (retainedMsgs.isEmpty()) {
                // not found
                continue
            }
            for (retainedMsg in retainedMsgs) {
                val retainedQos = retainedMsg.qosLevel()
                val qos = lowerQosToTheSubscriptionDesired(subscription, retainedQos)
//                val payloadBuf = Unpooled.wrappedBuffer(retainedMsg.payload)
                val payloadBuf = retainedMsg.payload.clone()
                targetSession?.sendRetainedPublishOnSessionAtQos(
                    retainedMsg.topic,
                    qos,
                    payloadBuf
                )
                // We made the buffer, we must release it.
//                payloadBuf.release()
            }
        }
    }

    /**
     * Create the SUBACK response from a list of topicFilters
     */
    private fun doAckMessageFromValidateFilters(
        topicFilters: List<MqttTopicSubscription>,
        messageId: Int
    ): MqttSubAckMessage {
        val grantedQoSLevels: MutableList<Int> = mutableListOf()
        topicFilters.forEach {
            grantedQoSLevels.add(it.qualityOfService().value())
        }
        val fixedHeader = MqttFixedHeader(
            MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE,
            false, 0
        )
        val payload = MqttSubAckPayload(grantedQoSLevels)
        return MqttSubAckMessage(fixedHeader, MqttMessageVariableHeader(messageId), payload)
    }

    suspend fun unsubscribe(topics: List<String>, mqttConnection: MQTTConnection, messageId: Int) {
        val clientID = mqttConnection.clientID
        for (t in topics) {
            val topic = Topic(t)
            if (topic.isNoValid()) {
                // close the connection, not valid topicFilter is a protocol violation
                mqttConnection.dropConnection()
                LOG.warn("Topic filter is not valid. topics: {}, offending topic filter: {}",
                    topics, topic)
                return
            }
            LOG.trace("Removing subscription topic={}", topic)
            subscriptions.removeSubscription(topic, clientID)

            // TODO remove the subscriptions to Session
//            clientSession.unsubscribeFrom(topic);
            interceptor.notifyTopicUnsubscribed(topic.toString(), clientID, mqttConnection.username)
        }
        // ack the client
        mqttConnection.sendUnsubAckMessage(topics, messageId)
    }

    suspend fun receivedPublishQos0(
        topic: Topic,
        username: String,
        clientID: String,
        msg: MqttPublishMessage
    ) {
        if (!authorizator.canWrite(topic, username, clientID)) {
            LOG.error("client is not authorized to publish on topic: {}", topic)
            return
        }
        publish2Subscribers(msg.payload(), topic, MqttQoS.AT_MOST_ONCE)
        if (msg.fixedHeader.isRetain) {
            // QoS == 0 && retain => clean old retained
            retainedRepository.cleanRetained(topic)
        }
        interceptor.notifyTopicPublished(msg, clientID, username)
    }

    suspend fun receivedPublishQos1(
        connection: MQTTConnection, topic: Topic, username: String, messageID: Int,
        msg: MqttPublishMessage
    ) {
        // verify if topic can be write
        topic.getTokens()
        if (topic.isNoValid()) {
            LOG.warn("Invalid topic format, force close the connection")
            connection.dropConnection()
            return
        }
        val clientId = connection.clientID
        if (!authorizator.canWrite(topic, username, clientId)) {
            LOG.error("MQTT client: {} is not authorized to publish on topic: {}", clientId, topic)
            return
        }
        val payload = msg.payload()
        publish2Subscribers(payload, topic, MqttQoS.AT_LEAST_ONCE)
        connection.sendPub(MqttMessageType.PUBACK, messageID)
        if (msg.fixedHeader.isRetain) {
            if (payload.isNotEmpty()) { //if (!payload.isReadable) {
                retainedRepository.cleanRetained(topic)
            } else {
                // before wasn't stored
                retainedRepository.retain(topic, msg)
            }
        }
        interceptor.notifyTopicPublished(msg, clientId, username)
    }

    private suspend fun publish2Subscribers(payload: ByteArray, topic: Topic, publishingQos: MqttQoS) {
        val topicMatchingSubscriptions = subscriptions.matchQosSharpening(topic)
        for (sub in topicMatchingSubscriptions) {
            val qos = lowerQosToTheSubscriptionDesired(sub, publishingQos)
            val targetSession = sessionRegistry.retrieve(sub.clientId)
            val isSessionPresent = targetSession != null
            if (isSessionPresent) {
                LOG.debug(
                    "Sending PUBLISH message to active subscriber CId: {}, topicFilter: {}, qos: {}",
                    sub.clientId, sub.topicFilter, qos)
                targetSession?.sendPublishOnSessionAtQos(topic, qos, payload)
            } else {
                // If we are, the subscriber disconnected after the subscriptions tree selected that session as a
                // destination.
                LOG.debug(
                    "PUBLISH to not yet present session. CId: {}, topicFilter: {}, qos: {}",
                    sub.clientId, sub.topicFilter, qos)
            }
        }
    }

    /**
     * First phase of a publish QoS2 protocol, sent by publisher to the broker. Publish to all interested
     * subscribers.
     */
    suspend fun receivedPublishQos2(
        connection: MQTTConnection, mqttPublishMessage: MqttPublishMessage, username: String
    ) {
        LOG.trace("Processing PUBREL message on connection: {}", connection)
        val topic = Topic(mqttPublishMessage.variableHeader().topicName)
        val payload = mqttPublishMessage.payload()
        val clientId = connection.clientID
        if (!authorizator.canWrite(topic, username, clientId)) {
            LOG.error("MQTT client is not authorized to publish on topic: {}", topic)
            return
        }
        publish2Subscribers(payload, topic, MqttQoS.EXACTLY_ONCE)
        val retained = mqttPublishMessage.fixedHeader.isRetain
        if (retained) {
            if (payload.isNotEmpty()) { //if (!payload.isReadable) {
                retainedRepository.cleanRetained(topic)
            } else {
                // before wasn't stored
                retainedRepository.retain(topic, mqttPublishMessage)
            }
        }
        interceptor.notifyTopicPublished(mqttPublishMessage, connection.clientID, username)
    }

    /**
     * Intended usage is only for embedded versions of the broker, where the hosting application
     * want to use the broker to send a publish message. Like normal external publish message but
     * with some changes to avoid security check, and the handshake phases for Qos1 and Qos2. It
     * also doesn't notifyTopicPublished because using internally the owner should already know
     * where it's publishing.
     *
     * @param msg
     * the message to publish
     */
    suspend fun internalPublish(msg: MqttPublishMessage) {
        val qos = msg.fixedHeader.qosLevel
        val topic = Topic(msg.variableHeader().topicName)
        val payload = msg.payload()
        LOG.info("Sending internal PUBLISH message Topic={}, qos={}", topic, qos)
        publish2Subscribers(payload, topic, qos)
        if (!msg.fixedHeader.isRetain) {
            return
        }
        if (qos == MqttQoS.AT_MOST_ONCE || payload.isEmpty()){// payload.readableBytes() == 0) {
            // QoS == 0 && retain => clean old retained
            retainedRepository.cleanRetained(topic)
            return
        }
        retainedRepository.retain(topic, msg)
    }

    /**
     * notify MqttConnectMessage after connection established (already pass login).
     * @param msg
     */
    fun dispatchConnection(msg: MqttConnectMessage) {
        interceptor.notifyClientConnected(msg)
    }

    fun dispatchDisconnection(clientId: String, userName: String) {
        interceptor.notifyClientDisconnected(clientId, userName)
    }

    fun dispatchConnectionLost(clientId: String, userName: String) {
        interceptor.notifyClientConnectionLost(clientId, userName)
    }

    fun dispatchShutDown(reason: String) {
        interceptor.notifyServerShuttingDown(reason)
    }
    //    void flushInFlight(MQTTConnection mqttConnection) {
    //        Session targetSession = sessionRegistry.retrieve(mqttConnection.getClientId());
    //        targetSession.flushAllQueuedMessages();
    //    }
    companion object {
        private val LOG = LoggerFactory.getLogger(PostOffice::class.java)
        fun lowerQosToTheSubscriptionDesired(sub: Subscription, qos: MqttQoS): MqttQoS {
            if (qos.value() > sub.requestedQos.value()) {
                return sub.requestedQos
            }
            return qos
        }
    }
}
