package io.zibi.komar.persistence

import io.zibi.komar.broker.IRetainedRepository
import io.zibi.komar.broker.RetainedMessage
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttPublishMessage
import org.h2.mvstore.MVMap
import org.h2.mvstore.MVStore

class H2RetainedRepository(mvStore: MVStore) : IRetainedRepository {
    private val queueMap: MVMap<Topic, RetainedMessage>

    init {
        queueMap = mvStore.openMap("retained_store")
    }

    override fun cleanRetained(topic: Topic) {
        queueMap.remove(topic)
    }

    override fun retain(topic: Topic, msg: MqttPublishMessage) {
        val payload = msg.content()
//        val rawPayload = ByteArray(payload.readableBytes())
//        payload.getBytes(0, rawPayload)
//        val toStore = RetainedMessage(topic, msg.fixedHeader.qosLevel, rawPayload)
        val toStore = RetainedMessage(topic, msg.fixedHeader.qosLevel, payload)
        queueMap[topic] = toStore
    }

    override val isEmpty: Boolean = queueMap.isEmpty()

    override fun retainedOnTopic(topic: String): List<RetainedMessage> {
        val searchTopic = Topic(topic)
        val matchingMessages: MutableList<RetainedMessage> = mutableListOf()
        for ((scanTopic, value) in queueMap) {
            if (scanTopic.match(searchTopic)) {
                matchingMessages.add(value)
            }
        }
        return matchingMessages
    }
}
