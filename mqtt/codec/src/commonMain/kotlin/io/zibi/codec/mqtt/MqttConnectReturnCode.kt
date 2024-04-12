package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.ReasonCode

/**
 * Return Code of [MqttConnAckMessage]
 */
enum class MqttConnectReturnCode(private val id: UByte) : ReasonCode {
    CONNECTION_ACCEPTED(0x00u),

    //MQTT 3 codes
    CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION(0X01u),
    CONNECTION_REFUSED_IDENTIFIER_REJECTED(0x02u),
    CONNECTION_REFUSED_SERVER_UNAVAILABLE(0x03u),
    CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD(0x04u),
    CONNECTION_REFUSED_NOT_AUTHORIZED(0x05u),

    //MQTT 5 codes
    CONNECTION_REFUSED_UNSPECIFIED_ERROR(0x80u),
    CONNECTION_REFUSED_MALFORMED_PACKET(0x81u),
    CONNECTION_REFUSED_PROTOCOL_ERROR(0x82u),
    CONNECTION_REFUSED_IMPLEMENTATION_SPECIFIC(0x83u),
    CONNECTION_REFUSED_UNSUPPORTED_PROTOCOL_VERSION(0x84u),
    CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID(0x85u),
    CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD(0x86u),
    CONNECTION_REFUSED_NOT_AUTHORIZED_5(0x87u),
    CONNECTION_REFUSED_SERVER_UNAVAILABLE_5(0x88u),
    CONNECTION_REFUSED_SERVER_BUSY(0x89u),
    CONNECTION_REFUSED_BANNED(0x8Au),
    CONNECTION_REFUSED_BAD_AUTHENTICATION_METHOD(0x8Cu),
    CONNECTION_REFUSED_TOPIC_NAME_INVALID(0x90u),
    CONNECTION_REFUSED_PACKET_TOO_LARGE(0x95u),
    CONNECTION_REFUSED_QUOTA_EXCEEDED(0x97u),
    CONNECTION_REFUSED_PAYLOAD_FORMAT_INVALID(0x99u),
    CONNECTION_REFUSED_RETAIN_NOT_SUPPORTED(0x9Au),
    CONNECTION_REFUSED_QOS_NOT_SUPPORTED(0x9Bu),
    CONNECTION_REFUSED_USE_ANOTHER_SERVER(0x9Cu),
    CONNECTION_REFUSED_SERVER_MOVED(0x9Du),
    CONNECTION_REFUSED_CONNECTION_RATE_EXCEEDED(0x9Fu);

    fun byteValue(): Byte {
        return id.toByte()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( id).toByteArray()

    override fun toDesc() = ReasonCode.makeDesc( this.name )


    companion object {
        fun valueOf(b: UByte): MqttConnectReturnCode = entries.find { it.id == b }
            ?: throw IllegalArgumentException("unknown connect return code: $b")

        fun connectReturnCodeForException(cause: Throwable): MqttConnectReturnCode {
            return when (cause) {
                is MqttUnacceptableProtocolVersionException ->  CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION
                is MqttIdentifierRejectedException ->  CONNECTION_REFUSED_IDENTIFIER_REJECTED
                else ->  CONNECTION_REFUSED_SERVER_UNAVAILABLE
            }
        }

    }
}
