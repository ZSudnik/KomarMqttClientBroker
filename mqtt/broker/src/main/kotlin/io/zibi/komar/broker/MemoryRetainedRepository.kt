package io.zibi.komar.broker

import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttPublishMessage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/*
* In memory retained messages store
* */
internal class MemoryRetainedRepository : IRetainedRepository {

    private val storage: ConcurrentMap<Topic, RetainedMessage> = ConcurrentHashMap()

    override fun cleanRetained(topic: Topic) {
        storage.remove(topic)
    }

    override fun retain(topic: Topic, msg: MqttPublishMessage) {
        val payload = msg.content()
//        val rawPayload = ByteArray(payload.readableBytes())
//        payload.getBytes(0, rawPayload)
//        val toStore = RetainedMessage(topic, msg.fixedHeader.qosLevel, rawPayload)
        val toStore = RetainedMessage(topic, msg.fixedHeader.qosLevel, payload)
        storage[topic] = toStore
    }

    override val isEmpty: Boolean
        get() = storage.isEmpty()

    override fun retainedOnTopic(topic: String): List<RetainedMessage> {
        val searchTopic = Topic(topic)
        val matchingMessages: MutableList<RetainedMessage> = mutableListOf()
        for ((scanTopic, value) in storage) {
            if (scanTopic.match(searchTopic)) {
                matchingMessages.add(value)
            }
        }
        return matchingMessages
    }
}
