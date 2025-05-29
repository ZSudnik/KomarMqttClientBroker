package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/suback](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#suback)
 */
class MqttSubAckMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttMessageVariableHeader?,
    payload: MqttSubAckPayload?
) : MqttMessage(mqttFixedHeader, variableHeader, payload) {

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun payload(): MqttSubAckPayload {
        return super.payload() as MqttSubAckPayload
    }

    override fun toDecByteArray(mqttVersion: MqttVersion) =
        mqttFixedHeader.toDecByteArray()  +
                remainingLength(
                    variableHeader().toDecByteArray() + payload().toDecByteArray()
                )

    companion object{
        fun create(messageId: Int, mqttQoSList: List<Int>): MqttSubAckMessage = MqttSubAckMessage(
            MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
            MqttMessageVariableHeader(messageId),
            MqttSubAckPayload(mqttQoSList)
        )
    }
}
