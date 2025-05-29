package io.zibi.komar.interception.messages

import io.zibi.codec.mqtt.MqttPublishMessage

class InterceptPublishMessage(
    private val msg: MqttPublishMessage,
    val clientID: String,
    val username: String
) : InterceptAbstractMessage(msg) {

    val topicName: String
        get() = msg.variableHeader().topicName
    val payload: ByteArray
        get() = msg.payload()
}
