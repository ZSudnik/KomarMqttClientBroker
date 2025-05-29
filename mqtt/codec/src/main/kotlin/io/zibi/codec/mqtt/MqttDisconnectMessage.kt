package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.Disconnect

/**
 * See [MQTTV3.1/disconnect](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#disconnect)
 */
class MqttDisconnectMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    private val variableHeader: MqttDisconnectVariableHeader?,
) : MqttMessage(mqttFixedHeader, variableHeader) {

    constructor(
        disconnectReasonCode: Disconnect = Disconnect.NORMAL_DISCONNECT,
        mqttVersion: MqttVersion
    ): this(
        MqttFixedHeader(
            MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0),
        if(mqttVersion.isMQTT_5()) MqttDisconnectVariableHeader(disconnectReasonCode) else null
        )

    override fun variableHeader(): MqttDisconnectVariableHeader {
        return super.variableHeader() as MqttDisconnectVariableHeader
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray{
        return mqttFixedHeader.toDecByteArray() +
                if(mqttVersion.isMQTT_5())  remainingLength(variableHeader().toDecByteArray())
                else byteArrayOf()
    }

}
