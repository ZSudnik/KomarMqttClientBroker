package io.zibi.komar.interception.messages

import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttQoS

abstract class InterceptAbstractMessage internal constructor(private val msg: MqttMessage) :
    InterceptMessage {
    val isRetainFlag: Boolean
        get() = msg.fixedHeader.isRetain
    val isDupFlag: Boolean
        get() = msg.fixedHeader.isDup
    val qos: MqttQoS
        get() = msg.fixedHeader.qosLevel
}
