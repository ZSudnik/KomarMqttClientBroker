package io.zibi.codec.mqtt

/**
 * See [
 * MQTTV3.1/subscribe](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#subscribe)
 */
class MqttSubscribeMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttMessageVariableHeader?,
    payload: MqttSubscribePayload?
) : MqttMessage(mqttFixedHeader, variableHeader, payload) {

    constructor(messageId: Int, mqttTopicSubscriptions: List<MqttTopicSubscription>): this(
        MqttFixedHeader(
            MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
            false, 0),
        MqttMessageVariableHeader(messageId),
        MqttSubscribePayload(mqttTopicSubscriptions)
    )

    constructor(messageId: Int, qos: Int = 0, topics: List<String>): this(
        MqttFixedHeader(
            MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
            false, 0),
        MqttMessageVariableHeader(messageId),
        MqttSubscribePayload(
            topics.map { topic -> MqttTopicSubscription(topic, MqttQoS.valueOf(qos)) }
        )
    )

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun payload(): MqttSubscribePayload {
        return super.payload() as MqttSubscribePayload
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray {
        return mqttFixedHeader.toDecByteArray() +
                remainingLength(variableHeader().toDecByteArray() +
                        payload().toDecByteArray())
    }
}