package io.zibi.codec.mqtt

/**
 * See [
 * MQTTV3.1/unsuback](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#unsuback)
 */
class MqttUnsubAckMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttMessageVariableHeader?,
    payload: MqttUnsubAckPayload? = null
) : MqttMessage(mqttFixedHeader, variableHeader, MqttUnsubAckPayload.withEmptyDefaults(payload)) {

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun payload(): MqttUnsubAckPayload {
        return super.payload() as MqttUnsubAckPayload
    }

    override fun toDecByteArray(mqttVersion: MqttVersion) = mqttFixedHeader.toDecByteArray() +
            remainingLength(
            variableHeader().toDecByteArray() + payload().toDecByteArray()
            )

    companion object{
        fun create(messageId: Int): MqttUnsubAckMessage = MqttUnsubAckMessage(
                MqttFixedHeader(
                    MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE,
                    false, 0),
            MqttMessageVariableHeader(messageId),
            null
        )
    }
}
