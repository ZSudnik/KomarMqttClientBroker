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

    companion object{
        fun create(messageId: Int, mqttTopicSubscriptions: List<MqttTopicSubscription>): MqttSubscribeMessage = MqttSubscribeMessage(
            MqttFixedHeader(
                MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE,
                false, 0),
            MqttMessageVariableHeader(messageId),
            MqttSubscribePayload(mqttTopicSubscriptions)
        )

        fun create(messageId: Int, qos: Int = 0, topics: List<String>): MqttSubscribeMessage {
            val list: MutableList<MqttTopicSubscription> = mutableListOf()
            for (topic in topics) {
                list.add(MqttTopicSubscription(topic, MqttQoS.valueOf(qos)))
            }
            return create(messageId, list)
        }
    }

}
