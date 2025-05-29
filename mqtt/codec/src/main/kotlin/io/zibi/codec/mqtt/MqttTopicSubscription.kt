package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttSubscriptionOption.Companion.onlyFromQos
import io.zibi.codec.mqtt.util.toDecByteArray

/**
 * Contains a topic name and Qos Level.
 * This is part of the [MqttSubscribePayload]
 */
class MqttTopicSubscription {
    val topicFilter: String
    val option: MqttSubscriptionOption

    constructor(topicFilter: String, qualityOfService: MqttQoS) {
        this.topicFilter = topicFilter
        option = onlyFromQos(
            qualityOfService
        )
    }

    constructor(topicFilter: String, option: MqttSubscriptionOption) {
        this.topicFilter = topicFilter
        this.option = option
    }

    fun qualityOfService(): MqttQoS {
        return option.qos
    }

    fun toDecByteArray() = topicFilter.toDecByteArray()  + option.toDecByteArray()

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null obj")
            .append('[')
            .append("topicFilter=").append(topicFilter)
            .append(", option=").append(option)
            .append(']')
            .toString()
    }
}
