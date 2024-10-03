package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT Auth message.
 */
sealed class Auth(override val byteValue: UByte) : ReasonCode(byteValue) {
    data object SUCCESS : Auth(0x00u)
    //sent by: Server
    data object CONTINUE_AUTHENTICATION : Auth(0x18u)
    //sent by: Client or Server
    data object REAUTHENTICATE : Auth(0x19u)


    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)

}