package io.zibi.komar.broker

import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttPublishMessage

interface IRetainedRepository {
    fun cleanRetained(topic: Topic)
    fun retain(topic: Topic, msg: MqttPublishMessage)
    val isEmpty: Boolean
    fun retainedOnTopic(topic: String): List<RetainedMessage>
}
