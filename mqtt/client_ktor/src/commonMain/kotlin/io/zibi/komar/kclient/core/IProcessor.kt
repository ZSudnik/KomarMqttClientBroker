package io.zibi.komar.mclient.core

import io.zibi.codec.mqtt.MqttMessage

interface IProcessor {
    fun processAck( msg: MqttMessage)
    fun cancel()
}