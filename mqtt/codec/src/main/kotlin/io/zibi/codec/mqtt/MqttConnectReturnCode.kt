package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.ReasonCode
/**
 * Return Code of [MqttConnAckMessage]
 */
sealed class MqttConnectReturnCode(override val byteValue: UByte) : ReasonCode(byteValue) {
    data object CONNECTION_ACCEPTED : MqttConnectReturnCode(0x00u)

    //MQTT 3 codes
    data object CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION : MqttConnectReturnCode(0X01u)
    data object CONNECTION_REFUSED_IDENTIFIER_REJECTED : MqttConnectReturnCode(0x02u)
    data object CONNECTION_REFUSED_SERVER_UNAVAILABLE : MqttConnectReturnCode(0x03u)
    data object CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD : MqttConnectReturnCode(0x04u)
    data object CONNECTION_REFUSED_NOT_AUTHORIZED : MqttConnectReturnCode(0x05u)

    //MQTT 5 codes
    data object CONNECTION_REFUSED_UNSPECIFIED_ERROR : MqttConnectReturnCode(0x80u)
    data object CONNECTION_REFUSED_MALFORMED_PACKET : MqttConnectReturnCode(0x81u)
    data object CONNECTION_REFUSED_PROTOCOL_ERROR : MqttConnectReturnCode(0x82u)
    data object CONNECTION_REFUSED_IMPLEMENTATION_SPECIFIC : MqttConnectReturnCode(0x83u)
    data object CONNECTION_REFUSED_UNSUPPORTED_PROTOCOL_VERSION : MqttConnectReturnCode(0x84u)
    data object CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID : MqttConnectReturnCode(0x85u)
    data object CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD : MqttConnectReturnCode(0x86u)
    data object CONNECTION_REFUSED_NOT_AUTHORIZED_5 : MqttConnectReturnCode(0x87u)
    data object CONNECTION_REFUSED_SERVER_UNAVAILABLE_5 : MqttConnectReturnCode(0x88u)
    data object CONNECTION_REFUSED_SERVER_BUSY : MqttConnectReturnCode(0x89u)
    data object CONNECTION_REFUSED_BANNED : MqttConnectReturnCode(0x8Au)
    data object CONNECTION_REFUSED_BAD_AUTHENTICATION_METHOD : MqttConnectReturnCode(0x8Cu)
    data object CONNECTION_REFUSED_TOPIC_NAME_INVALID : MqttConnectReturnCode(0x90u)
    data object CONNECTION_REFUSED_PACKET_TOO_LARGE : MqttConnectReturnCode(0x95u)
    data object CONNECTION_REFUSED_QUOTA_EXCEEDED : MqttConnectReturnCode(0x97u)
    data object CONNECTION_REFUSED_PAYLOAD_FORMAT_INVALID : MqttConnectReturnCode(0x99u)
    data object CONNECTION_REFUSED_RETAIN_NOT_SUPPORTED : MqttConnectReturnCode(0x9Au)
    data object CONNECTION_REFUSED_QOS_NOT_SUPPORTED : MqttConnectReturnCode(0x9Bu)
    data object CONNECTION_REFUSED_USE_ANOTHER_SERVER : MqttConnectReturnCode(0x9Cu)
    data object CONNECTION_REFUSED_SERVER_MOVED : MqttConnectReturnCode(0x9Du)
    data object CONNECTION_REFUSED_CONNECTION_RATE_EXCEEDED : MqttConnectReturnCode(0x9Fu)

    fun byteValue(): Byte {
        return byteValue.toByte()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun toDecByteArray() = ubyteArrayOf( byteValue ).toByteArray()

    override fun toDesc() = makeDesc(this::class.simpleName)

    companion object{
//        fun valueOf(b: UByte): MqttConnectReturnCode = sealedValues<MqttConnectReturnCode>(b)

        fun connectReturnCodeForException(cause: Throwable): MqttConnectReturnCode {
            return when (cause) {
                is MqttUnacceptableProtocolVersionException ->  CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION
                is MqttIdentifierRejectedException ->  CONNECTION_REFUSED_IDENTIFIER_REJECTED
                else ->  CONNECTION_REFUSED_SERVER_UNAVAILABLE
            }
        }
    }
}
