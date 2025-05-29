package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT SubAck message.
 */
sealed class SubAck(override val byteValue: UByte) : ReasonCode(byteValue) {
   data object GRANTED_QOS_0 : SubAck(0x00u)
   data object GRANTED_QOS_1 : SubAck(0x01u)
   data object GRANTED_QOS_2 : SubAck(0x02u)
   data object UNSPECIFIED_ERROR : SubAck(0x80u)
   data object IMPLEMENTATION_SPECIFIC_ERROR : SubAck(0x83u)
   data object NOT_AUTHORIZED : SubAck(0x87u)
   data object TOPIC_FILTER_INVALID : SubAck(0x8Fu)
   data object PACKET_IDENTIFIER_IN_USE : SubAck(0x91u)
   data object QUOTA_EXCEEDED : SubAck(0x97u)
   data object SHARED_SUBSCRIPTIONS_NOT_SUPPORTED : SubAck(0x9Eu)
   data object SUBSCRIPTION_IDENTIFIERS_NOT_SUPPORTED : SubAck(0xA1u)
   data object WILDCARD_SUBSCRIPTIONS_NOT_SUPPORTED : SubAck(0xA2u)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)
}