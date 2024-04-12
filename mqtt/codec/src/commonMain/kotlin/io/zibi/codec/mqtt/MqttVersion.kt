package io.zibi.codec.mqtt

import kotlin.text.Charsets.UTF_8

/**
 * Mqtt version specific constant values used by multiple classes in mqtt-codec.
 */
enum class MqttVersion(private val protocolName: String, private val level: Byte) {
    MQTT_3_1("MQIsdp", 3.toByte()),
    MQTT_3_1_1("MQTT", 4.toByte()),
    MQTT_5("MQTT", 5.toByte());

    fun protocolVersion(): Int = level.toInt()

    fun protocolName(): String {
        return protocolName
    }

    fun protocolNameBytes(): ByteArray {
        return protocolName.toByteArray(UTF_8)
    }

    fun protocolLevel(): Byte {
        return level
    }

    fun isMQTT_5(): Boolean = this == MQTT_5

    companion object {
        fun fromProtocolNameAndLevel(protocolName: String, protocolLevel: Byte): MqttVersion {
            return entries.find { it.level == protocolLevel && it.protocolName == protocolName}
                ?: throw MqttUnacceptableProtocolVersionException(
                    "Protocol name: $protocolName or level: $protocolLevel don't match")
        }
        fun fromLevel(protocolLevel: Int): MqttVersion {
            return entries.find { it.level.toInt() == protocolLevel}
                ?: throw MqttUnacceptableProtocolVersionException(
                    "Protocol level:$protocolLevel don't match")
        }
    }
}
