package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.util.toDecByteArrayOne

/**
 * Variable Header containing Packet Id, reason code and Properties as in MQTT v5 spec.
 */
class MqttPubReplyMessageVariableHeader(
    messageId: Int,
    val reasonCode: Byte,
    properties: MqttProperties?
) : MqttMessageVariableHeader(messageId) {
    private val properties: MqttProperties

    init {
        require(!(messageId < 1 || messageId > 0xffff)) { "messageId: $messageId (expected: 1 ~ 65535)" }
        this.properties = withEmptyDefaults(properties)
    }

    override fun properties(): MqttProperties {
        return properties
    }

    override fun toDecByteArray() = messageId.toDecByteArrayOne() + byteArrayOf(reasonCode) + properties.toDecByteArray()

    override fun toString(): String {
        return ""+(this::class.simpleName ?: "null object") + "[" +
                "messageId=" + messageId +
                ", reasonCode=" + reasonCode +
                ", properties=" + properties +
                ']'
    }

    companion object {
        const val REASON_CODE_OK: Byte = 0
    }
}
