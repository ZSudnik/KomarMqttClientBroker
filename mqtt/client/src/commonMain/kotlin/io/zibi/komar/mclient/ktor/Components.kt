package io.zibi.komar.mclient.ktor

import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttQoS

typealias MessageListener = suspend TopicContext.(message: MqttMessage) -> Unit


data class TopicSubscription(val topic: Topic, val qualityOfService: MqttQoS)

@JvmInline
value class Topic(val value: String)
