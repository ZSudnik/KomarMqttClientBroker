package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT PubRec message.
 */
sealed class PubRec(override val byteValue: UByte) : ReasonCode(byteValue) {
    data object SUCCESS : PubRec(0x00u)
    data object NO_MATCHING_SUBSCRIBERS : PubRec(0x10u)
    data object UNSPECIFIED_ERROR : PubRec(0x80u)
    data object IMPLEMENTATION_SPECIFIC_ERROR : PubRec(0x83u)
    data object NOT_AUTHORIZED : PubRec(0x87u)
    data object TOPIC_NAME_INVALID : PubRec(0x90u)
    data object PACKET_IDENTIFIER_IN_USE : PubRec(0x91u)
    data object QUOTA_EXCEEDED : PubRec(0x97u)
    data object PAYLOAD_FORMAT_INVALID : PubRec(0x99u)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)
}