package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT Auth message.
 */
enum class Auth( val byteValue: UByte) : ReasonCode {
    SUCCESS(0x00u),

    //sent by: Server
    CONTINUE_AUTHENTICATION(0x18u),

    //sent by: Client or Server
    REAUTHENTICATE(0x19u);


    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc(this.name)

    companion object {
        fun valueOf(b: UByte): Auth = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }
}