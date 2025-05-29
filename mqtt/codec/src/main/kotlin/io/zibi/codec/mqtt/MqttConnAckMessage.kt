package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/connack](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connack)
 */
class MqttConnAckMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    private val variableHeader: MqttConnAckVariableHeader?
) : MqttMessage(mqttFixedHeader, variableHeader) {

    constructor(returnCode: MqttConnectReturnCode, sessionPresent: Boolean):
    this(
        MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
        MqttConnAckVariableHeader(returnCode, sessionPresent)
    )

    override fun variableHeader(): MqttConnAckVariableHeader {
        return super.variableHeader() as MqttConnAckVariableHeader
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray{
        return mqttFixedHeader.toDecByteArray() +
                remainingLength(variableHeader().toDecByteArray())
    }

    companion object{
        fun create(returnCode: MqttConnectReturnCode, sessionPresent: Boolean): MqttConnAckMessage = MqttConnAckMessage(
            MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
            MqttConnAckVariableHeader(returnCode, sessionPresent)
        )
    }
}
