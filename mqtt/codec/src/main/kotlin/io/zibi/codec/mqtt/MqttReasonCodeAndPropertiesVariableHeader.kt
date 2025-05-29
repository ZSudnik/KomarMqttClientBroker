package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults

/**
 * Variable Header for AUTH and DISCONNECT messages represented by [MqttMessage]
 */
class MqttReasonCodeAndPropertiesVariableHeader(
    val reasonCode: Byte,
    properties: MqttProperties?
) {
    private val properties: MqttProperties

    init {
        this.properties = withEmptyDefaults(properties)
    }

    fun properties(): MqttProperties {
        return properties
    }

    fun toByteArray() = byteArrayOf(reasonCode) + properties.toDecByteArray()

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("reasonCode=").append(reasonCode.toInt())
            .append(", properties=").append(properties)
            .append(']')
            .toString()
    }

    companion object {
        const val REASON_CODE_OK: Byte = 0
    }
}
