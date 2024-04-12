package io.zibi.codec.mqtt.util

import io.zibi.codec.mqtt.MqttVersion

open class MqttConnectOptions (
    open var host: String = "127.0.0.1",
    open var port: Int = 1883, //8883
    open val mqttVersion: MqttVersion = MqttVersion.MQTT_3_1_1,
    open var isWillRetain: Boolean = false,
    open var willQos: Int = 0,
    open var isWillFlag: Boolean = false,
    open var isCleanSession: Boolean = false,
    open var keepAliveTime: Int = 60,
    open var actionTimeout: Long = 5000,
    open var connectTimeout: Long = 5000,
    open var clientIdentifier: String = "",
    open var willTopic: String = "",
    open var willMessage: ByteArray? = null,//  var willMessage: ByteArray? = null
    open var userName: String = "",
    open var password: ByteArray? = null, //  var password: ByteArray? = null
    ){
    val isHasUserName: Boolean
        get() = userName.isNotEmpty()

    val isHasPassword: Boolean
        get() = password != null && password!!.isNotEmpty()
}