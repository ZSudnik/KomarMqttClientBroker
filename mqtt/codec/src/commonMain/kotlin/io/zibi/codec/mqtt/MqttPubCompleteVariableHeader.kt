package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.reasoncode.PubComp

/**
 * Variable header of [MqttConnectMessage]
 */
class MqttPubCompleteVariableHeader(
    private val completeReasonCode: PubComp,
    properties: MqttProperties? = MqttProperties.NO_PROPERTIES
) {
    private val properties: MqttProperties

    init {
        this.properties = withEmptyDefaults(properties)
    }

    fun properties(): MqttProperties {
        return properties
    }

    fun toDecByteArray(): ByteArray{
        return completeReasonCode.toDecByteArray() + properties.toDecByteArray()
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("connectReturnCode=").append(completeReasonCode)
            .append(", properties=").append(properties)
            .append(']')
            .toString()
    }
}
