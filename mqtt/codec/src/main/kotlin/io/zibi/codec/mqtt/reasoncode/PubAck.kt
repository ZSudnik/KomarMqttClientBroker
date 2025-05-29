package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT PubAck message.
 */
sealed class PubAck(override val byteValue: UByte) : ReasonCode(byteValue) {
   data object SUCCESS : PubAck(0x00u)
   data object NO_MATCHING_SUBSCRIBERS : PubAck(0x10u)
   data object UNSPECIFIED_ERROR : PubAck(0x80u)
   data object IMPLEMENTATION_SPECIFIC_ERROR : PubAck(0x83u)
   data object NOT_AUTHORIZED : PubAck(0x87u)
   data object TOPIC_NAME_INVALID : PubAck(0x90u)
   data object PACKET_IDENTIFIER_IN_USE : PubAck(0x91u)
   data object QUOTA_EXCEEDED : PubAck(0x97u)
   data object PAYLOAD_FORMAT_INVALID : PubAck(0x99u)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)
}