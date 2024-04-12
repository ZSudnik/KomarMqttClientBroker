package io.zibi.codec.mqtt

/**
 * See [
 * MQTTV3.1/unsubscribe](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#unsubscribe)
 */
class MqttUnsubscribeMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttMessageVariableHeader?,
    payload: MqttUnsubscribePayload?
) : MqttMessage(mqttFixedHeader, variableHeader, payload) {

    constructor(messageId: Int, topicList: List<String>): this(
        MqttFixedHeader(
            MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_MOST_ONCE,
        false, 0x02),
        MqttMessageVariableHeader(messageId),
        MqttUnsubscribePayload(topicList)
    )

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun payload(): MqttUnsubscribePayload {
        return super.payload() as MqttUnsubscribePayload
    }

    override fun toDecByteArray(mqttVersion: MqttVersion) = mqttFixedHeader.toDecByteArray() +
            remainingLength(
                variableHeader().toDecByteArray() + payload().toDecByteArray()
            )

}
