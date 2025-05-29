package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT UnsubAck message.
 */
sealed class UnsubAck(override val byteValue: UByte) : ReasonCode(byteValue) {
   data object SUCCESS : UnsubAck(0x00u)
   data object NO_SUBSCRIPTION_EXISTED : UnsubAck(0x11u)
   data object UNSPECIFIED_ERROR : UnsubAck(0x80u)
   data object IMPLEMENTATION_SPECIFIC_ERROR : UnsubAck(0x83u)
   data object NOT_AUTHORIZED : UnsubAck(0x87u)
   data object TOPIC_FILTER_INVALID : UnsubAck(0x8Fu)
   data object PACKET_IDENTIFIER_IN_USE : UnsubAck(0x91u)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)
}