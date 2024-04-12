package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/pubcom](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#pubcom)
 */
class MqttPubCompleteMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    private val variableHeader: MqttPubCompleteVariableHeader?,
) : MqttMessage(mqttFixedHeader, variableHeader) {

    override fun variableHeader(): MqttPubCompleteVariableHeader {
        return super.variableHeader() as MqttPubCompleteVariableHeader
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray{
        return mqttFixedHeader.toDecByteArray() +
                remainingLength(variableHeader().toDecByteArray())
    }

}
