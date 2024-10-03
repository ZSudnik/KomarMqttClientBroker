package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT Disconnect message.
 */
sealed class Disconnect(override val byteValue: UByte) : ReasonCode(byteValue) {
    data object NORMAL_DISCONNECT : Disconnect(0x00u)
    //sent by: client or server
    data object DISCONNECT_WITH_WILL_MESSAGE : Disconnect(0x04u)
    //sent by: client
    data object UNSPECIFIED_ERROR : Disconnect(0x80u)
    //sent by: client or server
    data object MALFORMED_PACKET : Disconnect(0x81u)
    //sent by: client or server
    data object PROTOCOL_ERROR : Disconnect(0x82u)
    //sent by: client or server
    data object IMPLEMENTATION_SPECIFIC_ERROR : Disconnect(0x83u)
    //sent by: client or server
    data object NOT_AUTHORIZED : Disconnect(0x87u)
    //sent by: server
    data object SERVER_BUSY : Disconnect(0x89u)
    //sent by: server
    data object SERVER_SHUTTING_DOWN : Disconnect(0x8Bu)
    //sent by: server
    data object KEEP_ALIVE_TIMEOUT : Disconnect(0x8Du)
    //sent by: Server
    data object SESSION_TAKEN_OVER : Disconnect(0x8Eu)
    //sent by: Server
    data object TOPIC_FILTER_INVALID : Disconnect(0x8Fu)
    //sent by: Server
    data object TOPIC_NAME_INVALID : Disconnect(0x90u)
    //sent by: Client or Server
    data object RECEIVE_MAXIMUM_EXCEEDED : Disconnect(0x93u)
    //sent by: Client or Server
    data object TOPIC_ALIAS_INVALID : Disconnect(0x94u)
    //sent by: Client or Server
    data object PACKET_TOO_LARGE : Disconnect(0x95u)
    //sent by: Client or Server
    data object MESSAGE_RATE_TOO_HIGH : Disconnect(0x96u)
    //sent by: Client or Server
    data object QUOTA_EXCEEDED : Disconnect(0x97u)
    //sent by: Client or Server
    data object ADMINISTRATIVE_ACTION : Disconnect(0x98u)
    //sent by: Client or Server
    data object PAYLOAD_FORMAT_INVALID : Disconnect(0x99u)
    //sent by: Client or Server
    data object RETAIN_NOT_SUPPORTED : Disconnect(0x9Au)
    //sent by: Server
    data object QOS_NOT_SUPPORTED : Disconnect(0x9Bu)
    //sent by: Server
    data object USE_ANOTHER_SERVER : Disconnect(0x9Cu)
    //sent by: Server
    data object SERVER_MOVED : Disconnect(0x9Du)
    //sent by: Server
    data object SHARED_SUBSCRIPTIONS_NOT_SUPPORTED : Disconnect(0x9Eu)
    //sent by: Server
    data object CONNECTION_RATE_EXCEEDED : Disconnect(0x9Fu)
    //sent by: Server
    data object MAXIMUM_CONNECT_TIME : Disconnect(0xA0u)
    //sent by: Server
    data object SUBSCRIPTION_IDENTIFIERS_NOT_SUPPORTED : Disconnect(0xA1u)
    //sent by: Server
    data object WILDCARD_SUBSCRIPTIONS_NOT_SUPPORTED : Disconnect(0xA2u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)

}