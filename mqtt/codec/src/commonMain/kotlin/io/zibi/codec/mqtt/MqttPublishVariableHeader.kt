package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.exception.DecoderException
import io.zibi.codec.mqtt.util.toDecByteArray
import io.zibi.codec.mqtt.util.toDecByteArrayTwo

/**
 * Variable Header of the [MqttPublishMessage]
 */
class MqttPublishVariableHeader(
    val topicName: String,
    val packetId: Int,
    properties: MqttProperties? = MqttProperties.NO_PROPERTIES
) {
    private val properties: MqttProperties

    init {
        this.properties = withEmptyDefaults(properties)
    }

    fun toDecByteArray(mqttFixedHeader: MqttFixedHeader, version: MqttVersion): ByteArray {
        if (!isValidPublishTopicName(topicName)) {
            throw DecoderException("invalid publish topic name: $topicName (contains wildcards)")
        }
        var byteArray = topicName.toDecByteArray()
        if (mqttFixedHeader.qosLevel.value() > 0) {
            byteArray += packetId.toDecByteArrayTwo()
        }
        if (version == MqttVersion.MQTT_5) {
            byteArray += properties.toDecByteArray()
        }
        return byteArray
    }

    fun messageId(): Int {
        return packetId
    }

    fun properties(): MqttProperties {
        return properties
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("topicName=").append(topicName)
            .append(", packetId=").append(packetId)
            .append(']')
            .toString()
    }

    private fun isValidPublishTopicName(topicName: String): Boolean {
        // publish topic name must not contain any wildcard
        for (c in TOPIC_WILDCARDS) {
            if (topicName.indexOf(c) >= 0) {
                return false
            }
        }
        return true
    }

    companion object{
        val TOPIC_WILDCARDS = charArrayOf('#', '+')
    }

}
