package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.util.MqttConnectOptions

/**
 * See [MQTTV3.1/connect](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connect)
 */
class MqttConnectMessage(
    private val mqttFixedHeader: MqttFixedHeader,
    private val variableHeader: MqttConnectVariableHeader?,
    private val payload: MqttConnectPayload?
) : MqttMessage(mqttFixedHeader, variableHeader, payload) {

    constructor(options: MqttConnectOptions): this(
        MqttFixedHeader(
            MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE,
            false,10),
        MqttConnectVariableHeader(
            options.mqttVersion.protocolName(),
            (options.mqttVersion.protocolLevel().toInt() and 0xFF),
            options.isHasUserName,
            options.isHasPassword,
            options.isWillRetain,
            options.willQos,
            options.isWillFlag,
            options.isCleanSession,
            options.keepAliveTime
        ),
        MqttConnectPayload(
            options.clientIdentifier,
            options.willTopic,
            options.willMessage,
            options.userName,
            options.password
        )
    )

    override fun variableHeader(): MqttConnectVariableHeader {
        return super.variableHeader() as MqttConnectVariableHeader
    }

    override fun payload(): MqttConnectPayload {
        return super.payload() as MqttConnectPayload
    }

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray{
        return mqttFixedHeader.toDecByteArray() +
                remainingLength(
                    variableHeader().toDecByteArray() +
                            payload().toDecByteArray( variableHeader())
                )
    }

}
