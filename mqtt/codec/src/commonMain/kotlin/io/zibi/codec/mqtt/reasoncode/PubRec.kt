package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT PubRec message.
 */
enum class PubRec( val byteValue: UByte) : ReasonCode {
    SUCCESS(0x00u),
    NO_MATCHING_SUBSCRIBERS(0x10u),
    UNSPECIFIED_ERROR(0x80u),
    IMPLEMENTATION_SPECIFIC_ERROR(0x83u),
    NOT_AUTHORIZED(0x87u),
    TOPIC_NAME_INVALID(0x90u),
    PACKET_IDENTIFIER_IN_USE(0x91u),
    QUOTA_EXCEEDED(0x97u),
    PAYLOAD_FORMAT_INVALID(0x99u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )

    companion object {
        fun valueOf(b: UByte): PubRec = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }
}