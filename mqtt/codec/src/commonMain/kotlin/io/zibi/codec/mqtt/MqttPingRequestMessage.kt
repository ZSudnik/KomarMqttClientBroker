package io.zibi.codec.mqtt

/**
 * See [MQTTV3.1/connect](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#connect)
 */
class MqttPingRequestMessage(
    private val mqttFixedHeader: MqttFixedHeader,
) : MqttMessage(mqttFixedHeader, null, null) {

    constructor(): this(
        MqttFixedHeader(
            MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE,
            false, 0)
    )

    override fun toDecByteArray(mqttVersion: MqttVersion): ByteArray{
        return mqttFixedHeader.toDecByteArray() + byteArrayOf(0x00)
    }

 }
