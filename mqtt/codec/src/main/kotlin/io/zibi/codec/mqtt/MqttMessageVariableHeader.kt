package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.util.toDecByteArray

/**
 * Variable Header containing, Packet Id and Properties as in MQTT v5 spec.
 */
open class MqttMessageVariableHeader(val messageId: Int, properties: MqttProperties? = null)
{
    private val properties: MqttProperties

    init {
        require(!(messageId < 1 || messageId > 0xffff)) { "messageId: $messageId (expected: 1 ~ 65535)" }
        this.properties = withEmptyDefaults(properties)
    }

    open fun properties(): MqttProperties {
        return properties
    }

    open fun toDecByteArray() = messageId.toUShort().toDecByteArray() + properties.toDecByteArray()

    override fun toString(): String {
        return (this::class.simpleName?:"null object") + "[" +
                "messageId=" + messageId +
                ", properties=" + properties +
                ']'
    }

    fun withDefaultEmptyProperties(): MqttMessageVariableHeader {
        return this
    }
}
