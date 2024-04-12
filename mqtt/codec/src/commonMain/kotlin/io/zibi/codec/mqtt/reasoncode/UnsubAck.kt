package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT UnsubAck message.
 */
enum class UnsubAck(val byteValue: UByte) : ReasonCode {
    SUCCESS(0x00u),
    NO_SUBSCRIPTION_EXISTED(0x11u),
    UNSPECIFIED_ERROR(0x80u),
    IMPLEMENTATION_SPECIFIC_ERROR(0x83u),
    NOT_AUTHORIZED(0x87u),
    TOPIC_FILTER_INVALID(0x8Fu),
    PACKET_IDENTIFIER_IN_USE(0x91u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )

    companion object {
        fun valueOf(b: UByte): UnsubAck = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }

}