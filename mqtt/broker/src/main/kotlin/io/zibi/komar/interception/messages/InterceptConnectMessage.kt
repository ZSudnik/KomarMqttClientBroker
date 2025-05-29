package io.zibi.komar.interception.messages

import io.zibi.codec.mqtt.MqttConnectMessage

class InterceptConnectMessage(private val msg: MqttConnectMessage) : InterceptAbstractMessage(
    msg
) {
    val clientID: String
        get() = msg.payload().clientIdentifier()!!
    val isCleanSession: Boolean
        get() = msg.variableHeader().isCleanSession
    val keepAlive: Int
        get() = msg.variableHeader().keepAliveTimeSeconds
    val isPasswordFlag: Boolean
        get() = msg.variableHeader().hasPassword
    val protocolVersion: Byte
        get() = msg.variableHeader().version.toByte()
    val protocolName: String
        get() = msg.variableHeader().name
    val isUserFlag: Boolean
        get() = msg.variableHeader().hasUserName
    val isWillFlag: Boolean
        get() = msg.variableHeader().isWillFlag
    val willQos: Byte
        get() = msg.variableHeader().willQos.toByte()
    val isWillRetain: Boolean
        get() = msg.variableHeader().isWillRetain
    val username: String
        get() = msg.payload().userName()!!
    val password: ByteArray
        get() = msg.payload().passwordInBytes()!!
    val willTopic: String
        get() = msg.payload().willTopic()!!
    val willMessage: ByteArray
        get() = msg.payload().willMessageInBytes()!!
}
