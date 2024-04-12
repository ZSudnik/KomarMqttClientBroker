package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT PubRel message.
 */
enum class PubRel( val byteValue: UByte) : ReasonCode {
    SUCCESS(0x00u),
    PACKET_IDENTIFIER_NOT_FOUND(0x92u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )

    companion object {
        fun valueOf(b: UByte): PubRel = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }

}