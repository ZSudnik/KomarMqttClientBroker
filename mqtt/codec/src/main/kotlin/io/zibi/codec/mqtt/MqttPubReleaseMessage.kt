package io.zibi.codec.mqtt

/**
 * See [
 * MQTTV3.1/subscribe](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#subscribe)
 */
class MqttPubReleaseMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    variableHeader: MqttMessageVariableHeader?,
) : MqttMessage(mqttFixedHeader, variableHeader) {

    override fun variableHeader(): MqttMessageVariableHeader {
        return super.variableHeader() as MqttMessageVariableHeader
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray {
        return mqttFixedHeader.toDecByteArray() +
                remainingLength(variableHeader().toDecByteArray() )
    }

    companion object{
        fun create(messageId: Int, isDup: Boolean): MqttPubReleaseMessage = MqttPubReleaseMessage(
            MqttFixedHeader(
                MqttMessageType.PUBREL, isDup, MqttQoS.AT_LEAST_ONCE,
                false, 0),
            MqttMessageVariableHeader(messageId),
        )
    }

}
