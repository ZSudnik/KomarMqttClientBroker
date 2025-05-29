package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.util.toDecByteArray

/**
 * Variable header of [MqttConnectMessage]
 */
class MqttConnAckVariableHeader(
    private val connectReturnCode: MqttConnectReturnCode,
    val isSessionPresent: Boolean,
    properties: MqttProperties? = MqttProperties.NO_PROPERTIES
) {
    private val properties: MqttProperties

    init {
        this.properties = withEmptyDefaults(properties)
    }

    fun connectReturnCode(): MqttConnectReturnCode {
        return connectReturnCode
    }

    fun properties(): MqttProperties {
        return properties
    }

    fun toDecByteArray(): ByteArray{
        return connectReturnCode.toDecByteArray() + isSessionPresent.toDecByteArray()+ properties.toDecByteArray()
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("connectReturnCode=").append(connectReturnCode)
            .append(", sessionPresent=").append(isSessionPresent)
            .append(']')
            .toString()
    }
}
