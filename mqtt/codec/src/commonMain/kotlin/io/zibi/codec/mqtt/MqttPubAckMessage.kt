package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/puback](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#puback)
 */
class MqttPubAckMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    private val variableHeader: MqttMessageVariableHeader?
) : MqttMessage(mqttFixedHeader, variableHeader) {

    constructor(messageId: Int): this(
    MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
    MqttMessageVariableHeader(messageId)
    )

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun toDecByteArray(mqttVersion: MqttVersion) = mqttFixedHeader.toDecByteArray() +
            remainingLength( variableHeader().toDecByteArray() )

}
