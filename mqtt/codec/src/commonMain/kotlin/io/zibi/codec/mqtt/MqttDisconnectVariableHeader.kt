package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.reasoncode.Disconnect

/**
 * Variable header of [MqttConnectMessage]
 */
class MqttDisconnectVariableHeader(
    val disconnectReasonCode: Disconnect,
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
        return disconnectReasonCode.toDecByteArray() + properties.toDecByteArray()
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("connectReturnCode=").append(disconnectReasonCode)
            .append(", properties=").append(properties)
            .append(']')
            .toString()
    }
}
