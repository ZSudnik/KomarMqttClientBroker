package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT PubComp message.
 */
sealed class PubComp(override val byteValue: UByte) : ReasonCode(byteValue) {
    data object SUCCESS : PubComp(0x00u)
    data object PACKET_IDENTIFIER_NOT_FOUND : PubComp(0x92u)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)
}