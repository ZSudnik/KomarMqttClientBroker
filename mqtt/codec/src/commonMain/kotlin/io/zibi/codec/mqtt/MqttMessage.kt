package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.exception.DecoderException

/**
 * Base class for all MQTT message types.
 */
open class MqttMessage(
    val fixedHeader: MqttFixedHeader,
    private val variableHeader: Any? = null,
    private val payload: Any? = null,
) {

    open fun variableHeader(): Any? {
        return variableHeader
    }

    open fun payload(): Any? {
        return payload
    }

    open fun toDecByteArray(mqttVersion: MqttVersion): ByteArray = byteArrayOf()

    fun remainingLength(headerAndPayload: ByteArray): ByteArray {
        var size = headerAndPayload.size
        var result = byteArrayOf()
        var isNext: Boolean
        var loop = 0
        do {
            isNext = (size shr 7) > 0
            var newByte = size and 0x7F
            if (isNext) {
                newByte = newByte or 0x80
                size = size shr 7
            }
            result += byteArrayOf(newByte.toByte())
            loop++
        } while (isNext)
        if (loop > 4) {
            throw DecoderException("remaining length exceeds 4 digits {$loop}")
        }
        return result + headerAndPayload
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName ?: "null object")
            .append('[')
            .append("fixedHeader=")
            .append(fixedHeader.toString())
            .append(", variableHeader=")
            .append(if (variableHeader() != null) variableHeader.toString() else "")
            .append(", payload=").append(if (payload() != null) payload.toString() else "")
            .append(']')
            .toString()
    }

    companion object {
        // Constants for fixed-header only message types with all flags set to 0 (see
        // https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Table_2.2_-)
        val PINGREQ = MqttMessage(
            MqttFixedHeader(
                MqttMessageType.PINGREQ, false,
                MqttQoS.AT_MOST_ONCE, false, 0
            )
        )
        val PINGRESP = MqttMessage(
            MqttFixedHeader(
                MqttMessageType.PINGRESP, false,
                MqttQoS.AT_MOST_ONCE, false, 0
            )
        )
        val DISCONNECT = MqttMessage(
            MqttFixedHeader(
                MqttMessageType.DISCONNECT, false,
                MqttQoS.AT_MOST_ONCE, false, 0
            )
        )
    }
}