package io.zibi.komar.broker

import io.ktor.network.sockets.Socket
import io.zibi.komar.broker.SessionRegistry.SessionCreationResult
import io.zibi.komar.broker.security.IAuthenticator
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttConnAckMessage
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttConnectReturnCode
import io.zibi.codec.mqtt.MqttDisconnectMessage
import io.zibi.codec.mqtt.MqttFixedHeader
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttMessageVariableHeader
import io.zibi.codec.mqtt.MqttMessageType
import io.zibi.codec.mqtt.MqttPingResponseMessage
import io.zibi.codec.mqtt.MqttPubAckMessage
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.MqttPublishVariableHeader
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttSubAckMessage
import io.zibi.codec.mqtt.MqttSubscribeMessage
import io.zibi.codec.mqtt.MqttUnsubAckMessage
import io.zibi.codec.mqtt.MqttUnsubscribeMessage
import io.zibi.codec.mqtt.MqttVersion
import io.zibi.codec.mqtt.reasoncode.Disconnect
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.Volatile
import kotlin.math.roundToInt

class MQTTConnection internal constructor(
    val writeChannel: suspend (ByteArray) -> Unit,
    private val brokerConfig: BrokerConfiguration,
    private val authenticator: IAuthenticator,
    private val sessionRegistry: SessionRegistry,
    private val postOffice: PostOffice,
    private val mqttVersion: MqttVersion,
    private val socket: Socket,
) {

    private var keepAlive: Int = 0
    private var cleanSession: Boolean = true
    var clientID: String = ""
        private set
    var username: String = ""
        private set
    @Volatile
    var isConnected = false
        private set
    private val lastPacketId = AtomicInteger(0)
    private var bindedSession: Session? = null
    suspend fun handleMessage(msg: MqttMessage) {
        val messageType = msg.fixedHeader.messageType
        LOG.debug("Received MQTT message, type: {}", messageType)
        when (messageType) {
            MqttMessageType.CONNECT -> {
                val payload = (msg as MqttConnectMessage).payload()
                var clientId = payload.clientIdentifier()
                val username = payload.userName()
                LOG.trace("Processing CONNECT message. CId: {} username: {}", clientId, username)
                if (isNotProtocolVersion(msg, MqttVersion.MQTT_3_1) && isNotProtocolVersion(msg, MqttVersion.MQTT_3_1_1)) {
                    LOG.warn("MQTT protocol version is not valid. CId: {}", clientId)
                    abortConnection(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION)
                    return
                }
                val cleanSession = msg.variableHeader().isCleanSession
                if (clientId.isNullOrEmpty()) {
                    if (!brokerConfig.isAllowZeroByteClientId) {
                        LOG.info("Broker doesn't permit MQTT empty client ID. Username: {}", username)
                        abortConnection(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED)
                        return
                    }
                    if (!cleanSession) {
                        LOG.info("MQTT client ID cannot be empty for persistent session. Username: {}", username)
                        abortConnection(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED)
                        return
                    }
                    // Generating client id.
                    clientId = UUID.randomUUID().toString().replace("-", "")
                    LOG.debug("Client has connected with integration generated id: {}, username: {}", clientId, username)
                }
                if (!login(msg, clientId)) {
                    abortConnection(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD)
                    socket.close() //.addListener(CLOSE_ON_FAILURE)
                    return
                }
                val result: SessionCreationResult
                try {
                    LOG.trace("Binding MQTTConnection to session")
                    result = sessionRegistry.createOrReopenSession(msg, clientId, this.username)
                    result.session.bind(this)
                    bindedSession = result.session
                } catch (scex: SessionCorruptedException) {
                    LOG.warn("MQTT session for client ID {} cannot be created", clientId)
                    abortConnection(MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE)
                    return
                }
                val msgCleanSessionFlag = msg.variableHeader().isCleanSession
                val isSessionAlreadyPresent = !msgCleanSessionFlag && result.alreadyStored
                writeChannel(MqttConnAckMessage(
                    MqttConnectReturnCode.CONNECTION_ACCEPTED,
                    isSessionAlreadyPresent).toDecByteArray(mqttVersion))
//                     .addListener(object : ChannelFutureListener {
//                    @Throws(Exception::class)
//                    override fun operationComplete(future: ChannelFuture) {
//                        if (future.isSuccess) {
                if(bindedSession?.disconnected() == false){
//                            LOG.trace("CONNACK sent, channel: {}", channel)
                            if (!result.session.completeConnection()) {
//                                // send DISCONNECT and close the channel
                                writeChannel(
                                    MqttDisconnectMessage(Disconnect.CONNECTION_RATE_EXCEEDED,mqttVersion).toDecByteArray(mqttVersion))//.addListener(CLOSE)
                                LOG.warn("CONNACK is sent but the session created can't transition in CONNECTED state")
                            } else {
//                                NettyUtils.clientID(channel, clientIdUsed)
                                isConnected = true
                                // OK continue with sending queued messages and normal flow
                                if (result.mode == SessionRegistry.CreationModeEnum.REOPEN_EXISTING) {
                                    result.session.sendQueuedMessagesWhileOffline()
                                }
                                initializeKeepAliveTimeout( msg, clientId)
//                                channel.pipeline().addFirst("inflightResender", InflightResender(5000))
                                postOffice.dispatchConnection(msg)
                                LOG.trace("dispatch connection: {}", msg.toString())
                            }
                        } else {
                            bindedSession?.let {
                                it.disconnect()
                                sessionRegistry.remove(it)
                            }
                            LOG.error("CONNACK send failed, cleanup session and close the connection")
//                            channel.close()
                            socket.close()
                        }
//                    }
//                })
            }
            MqttMessageType.SUBSCRIBE -> {
                if (!isConnected) {
                    LOG.warn("SUBSCRIBE received on already closed connection")
                    dropConnection()
                    return
                }
                postOffice.subscribeClientToTopics(msg as MqttSubscribeMessage, clientID,
                    username, this)
            }
            MqttMessageType.UNSUBSCRIBE ->{
                val topics = (msg as MqttUnsubscribeMessage).payload().topics
                LOG.trace("Processing UNSUBSCRIBE message. topics: {}", topics)
                postOffice.unsubscribe(topics, this, msg.variableHeader().messageId)
            }
            MqttMessageType.PUBLISH -> {
                val qos = (msg as MqttPublishMessage).fixedHeader.qosLevel
                val topicName = msg.variableHeader().topicName
                val topic = Topic(topicName)
                val messageID = msg.variableHeader().packetId
                LOG.trace("Processing PUBLISH message, topic: {}, messageId: {}, qos: {}", topicName, messageID, qos)
                if (topic.isNoValid()) {
                    LOG.debug("Drop connection because of invalid topic format")
                    dropConnection()
                }
                when (qos) {
                    MqttQoS.AT_MOST_ONCE -> postOffice.receivedPublishQos0(topic, username, clientID, msg)
                    MqttQoS.AT_LEAST_ONCE -> postOffice.receivedPublishQos1(this, topic, username, messageID, msg)
                    MqttQoS.EXACTLY_ONCE -> {
                        bindedSession?.receivedPublishQos2(messageID, msg)
                        postOffice.receivedPublishQos2(this, msg, username)
                    }
                    else -> LOG.error("Unknown QoS-Type:{}", qos)
                }
            }
            MqttMessageType.PUBREC -> {
                val messageID = (msg.variableHeader() as MqttMessageVariableHeader).messageId
                bindedSession?.processPubRec(messageID)
            }
            MqttMessageType.PUBCOMP -> {
                val messageID = (msg.variableHeader() as MqttMessageVariableHeader).messageId
                bindedSession?.processPubComp(messageID)
            }
            MqttMessageType.PUBREL -> {
                val messageID = (msg.variableHeader() as MqttMessageVariableHeader).messageId
                bindedSession?.receivedPubRelQos2(messageID)
                sendPub(messageType, messageID)
            }
            MqttMessageType.DISCONNECT ->  {
                LOG.trace("Start DISCONNECT")
                if (!isConnected) {
                    LOG.info("DISCONNECT received on already closed connection")
                    return
                }
                bindedSession?.disconnect()
                isConnected = false
                socket.close() //.addListener(FIRE_EXCEPTION_ON_FAILURE)
                LOG.trace("Processed DISCONNECT")
                postOffice.dispatchDisconnection(clientID, username)
                LOG.trace("dispatch disconnection userName={}", username)
            }
            MqttMessageType.PUBACK ->  {
                val messageID = (msg.variableHeader() as MqttMessageVariableHeader).messageId
                bindedSession?.pubAckReceived(messageID)
            }
            MqttMessageType.PINGREQ -> {
                writeChannel(MqttPingResponseMessage().toDecByteArray(mqttVersion))
            //.addListener(CLOSE_ON_FAILURE)
            }

            else -> LOG.error("Unknown MessageType: {}", messageType)
        }
    }

    private fun initializeKeepAliveTimeout(
        msg: MqttConnectMessage,
        clientId: String
    ) {
        this.keepAlive = msg.variableHeader().keepAliveTimeSeconds
        this.cleanSession = msg.variableHeader().isCleanSession
        this.clientID = clientId
        val idleTime = (keepAlive * 1.5f).roundToInt()
//        if (channel.pipeline().names().contains("idleStateHandler")) channel.pipeline().remove("idleStateHandler")
//        channel.pipeline().addFirst("idleStateHandler", IdleStateHandler(idleTime, 0, 0))
        LOG.debug("Connection has been configured CId={}, keepAlive={}, removeTemporaryQoS2={}, idleTime={}", clientId, keepAlive, msg.variableHeader().isCleanSession, idleTime)
    }

    private fun isNotProtocolVersion(msg: MqttConnectMessage, version: MqttVersion): Boolean {
        return msg.variableHeader().version != version.protocolLevel().toInt()
    }

    private suspend fun abortConnection(returnCode: MqttConnectReturnCode) {
        val badProto = MqttConnAckMessage(returnCode, false)
        writeChannel(badProto.toDecByteArray(mqttVersion)) //.addListener(FIRE_EXCEPTION_ON_FAILURE)
        socket.close() //.addListener(CLOSE_ON_FAILURE)
    }

    private fun login(msg: MqttConnectMessage, clientId: String): Boolean {
        // handle user authentication
        if (msg.variableHeader().hasUserName) {
            var pwd: ByteArray? = null
            if (msg.variableHeader().hasPassword) {
                pwd = msg.payload().passwordInBytes()
            } else if (!brokerConfig.isAllowAnonymous) {
                LOG.info("Client didn't supply any password and MQTT anonymous mode is disabled CId={}", clientId)
                return false
            }
            val login = msg.payload().userName()
            if (!authenticator.checkValid(clientId, login, pwd)) {
                LOG.info("Authenticator has rejected the MQTT credentials CId={}, username={}", clientId, login)
                return false
            }
            if (login != null) {
                username = login
            }
        } else if (!brokerConfig.isAllowAnonymous) {
            LOG.info("Client didn't supply any credentials and MQTT anonymous mode is disabled. CId={}", clientId)
            return false
        }
        return true
    }

    suspend fun handleConnectionLost() {
        if (clientID.isEmpty()) return
        LOG.info("Notifying connection lost event")
        bindedSession?.let{ session ->
            session.will?.let {
                postOffice.fireWill(it)
            }
            if (session.isClean) {
                LOG.debug("Remove session for client")
                sessionRegistry.remove(session)
            } else {
                session.disconnect()
            }
        }
        isConnected = false
        //dispatch connection lost to intercept.
        postOffice.dispatchConnectionLost(clientID, username)
        LOG.trace("dispatch disconnection: userName={}", username)
    }

    fun dropConnection() {
        socket.close()//.addListener(FIRE_EXCEPTION_ON_FAILURE)
    }

    suspend fun sendSubAckMessage(messageID: Int, ackMessage: MqttSubAckMessage) {
        LOG.trace("Sending SUBACK response messageId: {}", messageID)
        writeChannel(ackMessage.toDecByteArray(mqttVersion))//.addListener(FIRE_EXCEPTION_ON_FAILURE)
    }

    suspend fun sendUnsubAckMessage(topics: List<String>, messageID: Int) {
        val fixedHeader = MqttFixedHeader(
            MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0
        )
        val ackMessage =
            MqttUnsubAckMessage(fixedHeader, MqttMessageVariableHeader(messageID))
        LOG.trace("Sending UNSUBACK message. messageId: {}, topics: {}", messageID, topics)
        writeChannel(ackMessage.toDecByteArray(mqttVersion))//.addListener(FIRE_EXCEPTION_ON_FAILURE)
        LOG.trace("Client unsubscribed from topics <{}>", topics)
    }

    suspend fun sendPub(messageType: MqttMessageType, messageID: Int) {
        LOG.trace("Sending {} for messageID: {}", messageType, messageID)
        val fixedHeader = MqttFixedHeader(messageType, false, MqttQoS.AT_MOST_ONCE, false, 0)
        val pubAckMessage = MqttPubAckMessage(fixedHeader, MqttMessageVariableHeader(messageID))
        sendIfWritableElseDrop(pubAckMessage)
    }

    suspend fun sendPublish(publishMsg: MqttPublishMessage) {
        val packetId = publishMsg.variableHeader().packetId
        val topicName = publishMsg.variableHeader().topicName
        val qos = publishMsg.fixedHeader.qosLevel
        if (LOG.isTraceEnabled) {
            LOG.trace("Sending PUBLISH({}) message. MessageId={}, topic={}, payload", qos, packetId, topicName)
        } else {
            LOG.debug("Sending PUBLISH({}) message. MessageId={}, topic={}", qos, packetId, topicName)
        }
        sendIfWritableElseDrop(publishMsg)
    }

    suspend fun sendIfWritableElseDrop(msg: MqttMessage) {
        if (LOG.isDebugEnabled) {
            LOG.debug("OUT {}", msg.fixedHeader.messageType)
        }
//        if (!writeChannel.isClosedForWrite){// .isWritable) {
            // Sending to external, retain a duplicate. Just retain is not
            // enough, since the receiver must have full control.
            val retainedDup: ByteArray = msg.toDecByteArray(mqttVersion)
//            if (msg is ByteBufHolder) {
//                retainedDup = (msg as ByteBufHolder).retainedDuplicate()
//            }
//            val channelFuture: ChannelFuture = if (brokerConfig.isImmediateBufferFlush) {
                writeChannel(retainedDup)
//            } else {
//                channel.write(retainedDup.size, retainedDup )
//            }
//            channelFuture.addListener(FIRE_EXCEPTION_ON_FAILURE)
//        }
    }

    suspend fun writabilityChanged() {
//        if (!writeChannel.isClosedForWrite) {
            LOG.debug("Channel is again writable")
            bindedSession?.writabilityChanged()
//        }
    }

    suspend fun sendPublishRetainedQos0(topic: Topic, qos: MqttQoS, payload: ByteArray) {
        val publishMsg = retainedPublish(topic.toString(), qos, payload)
        sendPublish(publishMsg)
    }

    suspend fun sendPublishRetainedWithPacketId(topic: Topic, qos: MqttQoS, payload: ByteArray) {
        val packetId = nextPacketId()
        val publishMsg = retainedPublishWithMessageId(topic.toString(), qos, payload, packetId)
        sendPublish(publishMsg)
    }

    // TODO move this method in Session
    suspend fun sendPublishNotRetainedQos0(topic: Topic, qos: MqttQoS, payload: ByteArray?) {
        val publishMsg = notRetainedPublish(topic.toString(), qos, payload)
        sendPublish(publishMsg)
    }

    suspend fun resendNotAckedPublishes() {
        bindedSession?.resendInflightNotAcked()
    }

    fun nextPacketId(): Int {
        return lastPacketId.updateAndGet { v: Int -> if (v == 65535) 1 else v + 1 }
    }

    override fun toString(): String {
        return "MQTTConnection{channel=$writeChannel, connected=$isConnected}"
    }

    fun remoteAddress(): String {
        return socket.remoteAddress.toString() //as InetSocketAddress
    }

    suspend fun readCompleted() {
        LOG.debug("readCompleted client CId: {}", clientID)
        if (clientID.isNotEmpty()) {
            // TODO drain all messages in target's session in-flight message queue
            bindedSession?.flushAllQueuedMessages()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MQTTConnection::class.java)
        fun pubrel(messageID: Int): MqttMessage {
            val pubRelHeader =
                MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0)
            return MqttMessage(pubRelHeader, MqttMessageVariableHeader(messageID))
        }

        private fun retainedPublish(
            topic: String,
            qos: MqttQoS,
            message: ByteArray
        ): MqttPublishMessage {
            return retainedPublishWithMessageId(topic, qos, message, 0)
        }

        private fun retainedPublishWithMessageId(
            topic: String, qos: MqttQoS, message: ByteArray,
            messageId: Int
        ): MqttPublishMessage {
            val fixedHeader = MqttFixedHeader(MqttMessageType.PUBLISH, false, qos, true, 0)
            val varHeader = MqttPublishVariableHeader(topic, messageId)
            return MqttPublishMessage(fixedHeader, varHeader, message)
        }

        fun notRetainedPublish(
            topic: String,
            qos: MqttQoS,
            message: ByteArray?
        ): MqttPublishMessage {
            return notRetainedPublishWithMessageId(topic, qos, message, 0)
        }

        fun notRetainedPublishWithMessageId(
            topic: String, qos: MqttQoS, message: ByteArray?,
            messageId: Int
        ): MqttPublishMessage {
            val fixedHeader = MqttFixedHeader(MqttMessageType.PUBLISH, false, qos, false, 0)
            val varHeader = MqttPublishVariableHeader(topic, messageId)
            return MqttPublishMessage(fixedHeader, varHeader, message)
        }
    }
}
