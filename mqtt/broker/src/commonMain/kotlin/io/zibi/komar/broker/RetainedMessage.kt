package io.zibi.komar.broker

import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttQoS
import java.io.Serializable

class RetainedMessage( val topic: Topic, private val qos: MqttQoS, val payload: ByteArray) :
    Serializable {

    fun qosLevel(): MqttQoS {
        return qos
    }
}
