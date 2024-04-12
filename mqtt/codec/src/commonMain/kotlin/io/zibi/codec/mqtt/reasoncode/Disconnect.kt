package io.zibi.codec.mqtt.reasoncode

/**
 * Provides a set of enumeration that exposes standard MQTT 5 reason codes used by various messages.
 * Reason codes for MQTT Disconnect message.
 */
enum class Disconnect( val byteValue: UByte) : ReasonCode {
    NORMAL_DISCONNECT(0x00u),
    //sent by: client or server
    DISCONNECT_WITH_WILL_MESSAGE(0x04u),
    //sent by: client
    UNSPECIFIED_ERROR(0x80u),
    //sent by: client or server
    MALFORMED_PACKET(0x81u),
    //sent by: client or server
    PROTOCOL_ERROR(0x82u),
    //sent by: client or server
    IMPLEMENTATION_SPECIFIC_ERROR(0x83u),
    //sent by: client or server
    NOT_AUTHORIZED(0x87u),
    //sent by: server
    SERVER_BUSY(0x89u),
    //sent by: server
    SERVER_SHUTTING_DOWN(0x8Bu),
    //sent by: server
    KEEP_ALIVE_TIMEOUT(0x8Du),
    //sent by: Server
    SESSION_TAKEN_OVER(0x8Eu),
    //sent by: Server
    TOPIC_FILTER_INVALID(0x8Fu),
    //sent by: Server
    TOPIC_NAME_INVALID(0x90u),
    //sent by: Client or Server
    RECEIVE_MAXIMUM_EXCEEDED(0x93u),
    //sent by: Client or Server
    TOPIC_ALIAS_INVALID(0x94u),
    //sent by: Client or Server
    PACKET_TOO_LARGE(0x95u),
    //sent by: Client or Server
    MESSAGE_RATE_TOO_HIGH(0x96u),
    //sent by: Client or Server
    QUOTA_EXCEEDED(0x97u),
    //sent by: Client or Server
    ADMINISTRATIVE_ACTION(0x98u),
    //sent by: Client or Server
    PAYLOAD_FORMAT_INVALID(0x99u),
    //sent by: Client or Server
    RETAIN_NOT_SUPPORTED(0x9Au),
    //sent by: Server
    QOS_NOT_SUPPORTED(0x9Bu),
    //sent by: Server
    USE_ANOTHER_SERVER(0x9Cu),
    //sent by: Server
    SERVER_MOVED(0x9Du),
    //sent by: Server
    SHARED_SUBSCRIPTIONS_NOT_SUPPORTED(0x9Eu),
    //sent by: Server
    CONNECTION_RATE_EXCEEDED(0x9Fu),
    //sent by: Server
    MAXIMUM_CONNECT_TIME(0xA0u),
    //sent by: Server
    SUBSCRIPTION_IDENTIFIERS_NOT_SUPPORTED(0xA1u),
    //sent by: Server
    WILDCARD_SUBSCRIPTIONS_NOT_SUPPORTED(0xA2u);

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )

    companion object {
        fun valueOf(b: UByte): Disconnect = entries.find { it.byteValue == b }
            ?: throw IllegalArgumentException("unknown reason code: $b")
    }

}