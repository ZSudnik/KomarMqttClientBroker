package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT SubAck message.
 */
enum class SubAck( val byteValue: UByte) : ReasonCode {
    GRANTED_QOS_0(0x00u),
    GRANTED_QOS_1(0x01u),
    GRANTED_QOS_2(0x02u),
    UNSPECIFIED_ERROR(0x80u),
    IMPLEMENTATION_SPECIFIC_ERROR(0x83u),
    NOT_AUTHORIZED(0x87u),
    TOPIC_FILTER_INVALID(0x8Fu),
    PACKET_IDENTIFIER_IN_USE(0x91u),
    QUOTA_EXCEEDED(0x97u),
    SHARED_SUBSCRIPTIONS_NOT_SUPPORTED(0x9Eu),
    SUBSCRIPTION_IDENTIFIERS_NOT_SUPPORTED(0xA1u),
    WILDCARD_SUBSCRIPTIONS_NOT_SUPPORTED(0xA2u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )

    companion object {
        fun valueOf(b: UByte): SubAck = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }
}