package com.zibi.service.client.service

import io.zibi.codec.mqtt.MqttVersion
import io.zibi.codec.mqtt.util.MqttConnectOptions

class ClientProperties(
    override var host: String = "192.168.73.25",
    override var port: Int = 1883, //8883
    override val mqttVersion: MqttVersion = MqttVersion.MQTT_3_1_1,
    override var isWillRetain: Boolean = false,
    override var willQos: Int = 1,
    override var isWillFlag: Boolean = false,
    override var isCleanSession: Boolean = true,
    override var keepAliveTime: Int = 500,
    override var clientIdentifier: String = "zibi_mqtt",
    override var willTopic: String = "/tasmota/#",
    override var willMessage: ByteArray? = null,
    override var userName: String = "Zibi",
    override var password: ByteArray? = null,
    override var actionTimeout: Long = 5000L,
    override var connectTimeout: Long = 5000,
): MqttConnectOptions()