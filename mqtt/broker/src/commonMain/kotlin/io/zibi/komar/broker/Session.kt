package io.zibi.komar.broker

import io.zibi.komar.BrokerConstants.FLIGHT_BEFORE_RESEND_MS
import io.zibi.komar.BrokerConstants.INFLIGHT_WINDOW_SIZE
import io.zibi.komar.broker.SessionRegistry.EnqueuedMessage
import io.zibi.komar.broker.SessionRegistry.PubRelMarker
import io.zibi.komar.broker.SessionRegistry.PublishedMessage
import io.zibi.komar.broker.subscriptions.Subscription
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.komar.broker.Session.SessionStatus.*
import io.zibi.codec.mqtt.MqttFixedHeader
import io.zibi.codec.mqtt.MqttMessageType
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.MqttPublishVariableHeader
import io.zibi.codec.mqtt.MqttQoS
import org.slf4j.LoggerFactory
import java.util.Queue
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Session(clientId: String, clean: Boolean, sessionQueue: Queue<EnqueuedMessage>) {

    internal class InFlightPacket(val packetId: Int, delayInMilliseconds: Long) : Delayed {
        private val startTime: Long

        init {
            startTime = System.currentTimeMillis() + delayInMilliseconds
        }

        override fun getDelay(unit: TimeUnit): Long {
            val diff = startTime - System.currentTimeMillis()
            return unit.convert(diff, TimeUnit.MILLISECONDS)
        }

        override fun compareTo(other: Delayed): Int {
            if (startTime - (other as InFlightPacket).startTime == 0L) {
                return 0
            }
            return if (startTime - other.startTime > 0) {
                1
            } else {
                -1
            }
        }
    }

    enum class SessionStatus {
        CONNECTED,
        CONNECTING,
        DISCONNECTING,
        DISCONNECTED
    }

    class Will(
        val topic: String,
        val payload: ByteArray,
        val qos: MqttQoS,
        val retained: Boolean
    )

    val clientID: String
    var isClean: Boolean
        private set
    var will: Will? = null
        private set
    private val sessionQueue: Queue<EnqueuedMessage>
    val status = AtomicReference(DISCONNECTED)
    val subscriptions: MutableList<Subscription> = mutableListOf()
    private var mqttConnection: MQTTConnection? = null
    private val inflightWindow: MutableMap<Int, EnqueuedMessage> = HashMap()
    private val inflightTimeouts = DelayQueue<InFlightPacket>()
    private val qos2Receiving: MutableMap<Int, MqttPublishMessage> = HashMap()
    private val inflightSlots = AtomicInteger(INFLIGHT_WINDOW_SIZE) // this should be configurable

    constructor(
        clientId: String,
        clean: Boolean,
        will: Will,
        sessionQueue: Queue<EnqueuedMessage>
    ) : this(clientId, clean, sessionQueue) {
        this.will = will
    }

    init {
        clientID = clientId
        isClean = clean
        this.sessionQueue = sessionQueue
    }

    fun update(clean: Boolean, will: Will?) {
        isClean = clean
        this.will = will
    }

    fun markConnecting() {
        assignState(DISCONNECTED, CONNECTING)
    }

    fun completeConnection(): Boolean {
        return assignState(CONNECTING, CONNECTED)
    }

    fun bind(mqttConnection: MQTTConnection) {
        this.mqttConnection = mqttConnection
    }

    fun disconnected(): Boolean {
        return status.get() == DISCONNECTED
    }

    fun connected(): Boolean {
        return status.get() == CONNECTED
    }

    fun assignState(expected: SessionStatus, newState: SessionStatus): Boolean {
        return status.compareAndSet(expected, newState)
    }

    fun closeImmediately() {
        mqttConnection?.dropConnection()
    }

    fun disconnect() {
        val res = assignState(CONNECTED, DISCONNECTING)
        if (!res) {
            // someone already moved away from CONNECTED
            // TODO what to do?
            return
        }
        mqttConnection = null
        will = null
        assignState(DISCONNECTING, DISCONNECTED)
    }

    suspend fun processPubRec(pubRecPacketId: Int) {
        // Message discarded, make sure any buffers in it are released
        val removed = inflightWindow.remove(pubRecPacketId)
        if (removed == null) {
            LOG.warn("Received a PUBREC with not matching packetId")
            return
        }
        if (removed is PubRelMarker) {
            LOG.info("Received a PUBREC for packetId that was already moved in second step of Qos2")
            return
        }
        inflightWindow[pubRecPacketId] = PubRelMarker()
        inflightTimeouts.add(InFlightPacket(pubRecPacketId, FLIGHT_BEFORE_RESEND_MS.toLong()))
        val pubRel = MQTTConnection.pubrel(pubRecPacketId)
        mqttConnection!!.sendIfWritableElseDrop(pubRel)
        drainQueueToConnection()
    }

    suspend fun processPubComp(messageID: Int) {
        // Message discarded, make sure any buffers in it are released
        val removed = inflightWindow.remove(messageID)
        if (removed == null) {
            LOG.warn("Received a PUBCOMP with not matching packetId")
            return
        }
        removed.release()
        inflightSlots.incrementAndGet()
        drainQueueToConnection()

        // TODO notify the interceptor
//                final InterceptAcknowledgedMessage interceptAckMsg = new InterceptAcknowledgedMessage(inflightMsg,
// topic, username, messageID);
//                m_interceptor.notifyMessageAcknowledged(interceptAckMsg);
    }

    suspend fun sendPublishOnSessionAtQos(topic: Topic, qos: MqttQoS?, payload: ByteArray) {
        when (qos) {
            MqttQoS.AT_MOST_ONCE -> if (connected()) {
                mqttConnection!!.sendPublishNotRetainedQos0(topic, qos, payload)
            }
            MqttQoS.AT_LEAST_ONCE -> sendPublishQos1(topic, qos, payload)
            MqttQoS.EXACTLY_ONCE -> sendPublishQos2(topic, qos, payload)
            MqttQoS.FAILURE -> LOG.error("Not admissible")
            else -> {}
        }
    }

    private suspend fun sendPublishQos1(topic: Topic, qos: MqttQoS, payload: ByteArray) {
        if (!connected() && isClean) {
            //pushing messages to disconnected not clean session
            return
        }
        if (canSkipQueue()) {
            inflightSlots.decrementAndGet()
            val packetId = mqttConnection!!.nextPacketId()

            // Adding to a map, retain.
//            payload.retain() TODO ??
            val old = inflightWindow.put(packetId, PublishedMessage(topic, qos, payload))
            // If there already was something, release it.
            old?.let {
                it.release()
                inflightSlots.incrementAndGet()
            }
            inflightTimeouts.add(InFlightPacket(packetId, FLIGHT_BEFORE_RESEND_MS.toLong()))
            val publishMsg = MQTTConnection.notRetainedPublishWithMessageId(
                topic.toString(), qos,
                payload, packetId
            )
            mqttConnection!!.sendPublish(publishMsg)
//            LOG.debug("Write direct to the peer, inflight slots: {}", inflightSlots.get())
//            if (inflightSlots.get() == 0) {
//                mqttConnection!!.flush()
//            }

            // TODO drainQueueToConnection();?
        } else {
            val msg = PublishedMessage(topic, qos, payload)
            // Adding to a queue, retain.
            msg.retain()
            sessionQueue.add(msg)
            LOG.debug("Enqueue to peer session")
        }
    }

    private suspend fun sendPublishQos2(topic: Topic, qos: MqttQoS, payload: ByteArray) {
        if (canSkipQueue()) {
            inflightSlots.decrementAndGet()
            val packetId = mqttConnection!!.nextPacketId()

            // Retain before adding to map
//            payload.retain() TODO ??
            val old = inflightWindow.put(packetId, PublishedMessage(topic, qos, payload))
            // If there already was something, release it.
            old?.let {
                it.release()
                inflightSlots.incrementAndGet()
            }
            inflightTimeouts.add(InFlightPacket(packetId, FLIGHT_BEFORE_RESEND_MS.toLong()))
            val publishMsg = MQTTConnection.notRetainedPublishWithMessageId(
                topic.toString(), qos,
                payload, packetId
            )
            mqttConnection!!.sendPublish(publishMsg)
            drainQueueToConnection()
        } else {
            val msg = PublishedMessage(topic, qos, payload)
            // Adding to a queue, retain.
            msg.retain()
            sessionQueue.add(msg)
        }
    }

    private fun canSkipQueue(): Boolean {
        return sessionQueue.isEmpty() && inflightSlots.get() > 0 && connected()
//                && !mqttConnection!!.writeChannel.isClosedForWrite //isWritable
    }

    private fun inflighHasSlotsAndConnectionIsUp(): Boolean {
        return inflightSlots.get() > 0 && connected()
//                && !mqttConnection!!.writeChannel.isClosedForWrite //isWritable
    }

    suspend fun pubAckReceived(ackPacketId: Int) {
        // TODO remain to invoke in somehow m_interceptor.notifyMessageAcknowledged
        val removed = inflightWindow.remove(ackPacketId)
        if (removed == null) {
            LOG.warn("Received a PUBACK with not matching packetId")
            return
        }
        removed.release()
        inflightSlots.incrementAndGet()
        drainQueueToConnection()
    }

    suspend fun flushAllQueuedMessages() {
        drainQueueToConnection()
    }

    suspend fun resendInflightNotAcked() {
        val expired: MutableCollection<InFlightPacket> = ArrayList<InFlightPacket>(INFLIGHT_WINDOW_SIZE)
        inflightTimeouts.drainTo(expired)
        debugLogPacketIds(expired)
        for (notAckPacketId in expired) {
            val msg = inflightWindow[notAckPacketId.packetId]
                ?: // Already acked...
                continue
            if (msg is PubRelMarker) {
                val pubRel = MQTTConnection.pubrel(notAckPacketId.packetId)
                inflightTimeouts.add(
                    InFlightPacket(
                        notAckPacketId.packetId,
                        FLIGHT_BEFORE_RESEND_MS.toLong()
                    )
                )
                mqttConnection!!.sendIfWritableElseDrop(pubRel)
            } else {
                val pubMsg = msg as PublishedMessage
                val topic = pubMsg.topic
                val qos = pubMsg.publishingQos
                val copiedPayload = pubMsg.payload.clone() // .retainedDuplicate()
                val publishMsg =
                    publishNotRetainedDuplicated(notAckPacketId, topic, qos, copiedPayload)
                inflightTimeouts.add(
                    InFlightPacket(
                        notAckPacketId.packetId,
                        FLIGHT_BEFORE_RESEND_MS.toLong()
                    )
                )
                mqttConnection!!.sendPublish(publishMsg)
            }
        }
    }

    private fun debugLogPacketIds(expired: Collection<InFlightPacket>) {
        if (!LOG.isDebugEnabled || expired.isEmpty()) {
            return
        }
        val sb = StringBuilder()
        expired.forEach { packet ->
            sb.append(packet.packetId).append(", ")
        }
        LOG.debug("Resending {} in flight packets [{}]", expired.size, sb)
    }

    private fun publishNotRetainedDuplicated(
        notAckPacketId: InFlightPacket, topic: Topic?, qos: MqttQoS,
        payload: ByteArray
    ): MqttPublishMessage {
        val fixedHeader = MqttFixedHeader(MqttMessageType.PUBLISH, true, qos, false, 0)
        val varHeader = MqttPublishVariableHeader(topic.toString(), notAckPacketId.packetId)
        return MqttPublishMessage(fixedHeader, varHeader, payload)
    }

    private suspend fun drainQueueToConnection() {
        // consume the queue
        while (!sessionQueue.isEmpty() && inflighHasSlotsAndConnectionIsUp()) {
            val msg = sessionQueue.poll()
                ?: // Our message was already fetched by another Thread.
                return
            inflightSlots.decrementAndGet()
            val sendPacketId = mqttConnection!!.nextPacketId()

            // Putting it in a map, but the retain is cancelled out by the below release.
            val old = inflightWindow.put(sendPacketId, msg)
            old?.let {
                it.release()
                inflightSlots.incrementAndGet()
            }
            inflightTimeouts.add(InFlightPacket(sendPacketId, FLIGHT_BEFORE_RESEND_MS.toLong()))
            val msgPub = msg as PublishedMessage
            val publishMsg = MQTTConnection.notRetainedPublishWithMessageId(
                msgPub.topic.toString(),
                msgPub.publishingQos,
                msgPub.payload, sendPacketId
            )
            mqttConnection!!.sendPublish(publishMsg)

            // we fetched msg from a map, but the release is cancelled out by the above retain
        }
    }

    suspend fun writabilityChanged() {
        drainQueueToConnection()
    }

    suspend fun sendQueuedMessagesWhileOffline() {
        LOG.trace("Republishing all saved messages for session {}", this)
        drainQueueToConnection()
    }

    suspend fun sendRetainedPublishOnSessionAtQos(topic: Topic, qos: MqttQoS, payload: ByteArray) {
        if (qos != MqttQoS.AT_MOST_ONCE) {
            // QoS 1 or 2
            mqttConnection!!.sendPublishRetainedWithPacketId(topic, qos, payload)
        } else {
            mqttConnection!!.sendPublishRetainedQos0(topic, qos, payload)
        }
    }

    suspend fun receivedPublishQos2(messageID: Int, msg: MqttPublishMessage) {
        // Retain before putting msg in map.
//        ReferenceCountUtil.retain(msg) TODO
//        val old = qos2Receiving.put(messageID, msg)
        // In case of evil client with duplicate msgid.
//        ReferenceCountUtil.release(old) TODO
        mqttConnection!!.sendPub(MqttMessageType.PUBREC,messageID)
    }

    fun receivedPubRelQos2(messageID: Int) {
        // Done with the message, remove from queue and release payload.
//        val removedMsg = qos2Receiving.remove(messageID)
//        ReferenceCountUtil.release(removedMsg) TODO
    }

    fun remoteAddress(): String? {
        return if (connected()) {
            mqttConnection?.remoteAddress()
        } else null
    }

    override fun toString(): String {
        return "Session{" +
                "clientId='" + clientID + '\'' +
                ", clean=" + isClean +
                ", status=" + status +
                ", inflightSlots=" + inflightSlots +
                '}'
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Session::class.java)
    }
}
