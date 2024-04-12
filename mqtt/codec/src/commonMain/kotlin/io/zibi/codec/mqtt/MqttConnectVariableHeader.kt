package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.util.toDecByteArray
import io.zibi.codec.mqtt.util.toDecByteArrayOne

/**
 * Variable Header for the [MqttConnectMessage]
 */
class MqttConnectVariableHeader(
    val name: String,
    val version: Int,
    val hasUserName: Boolean,
    val hasPassword: Boolean,
    val isWillRetain: Boolean,
    val willQos: Int,
    val isWillFlag: Boolean,
    val isCleanSession: Boolean,
    val keepAliveTimeSeconds: Int,
    properties: MqttProperties? = MqttProperties.NO_PROPERTIES
) {
    private val properties: MqttProperties

    init {
        this.properties = withEmptyDefaults(properties)
    }

    fun properties(): MqttProperties {
        return properties
    }

    fun toDecByteArray(): ByteArray {
        var byteArray = byteArrayOf()
        byteArray += name.toDecByteArray() + version.toDecByteArrayOne()
        var b1 = 0
        if(hasUserName) b1 = b1  or 0x80
        if(hasPassword) b1 = b1 or 0x40
        if(isWillRetain) b1 = b1 or 0x20
        b1 = b1 or (willQos shl 3)
        if(isWillFlag) b1 = b1 or 0x04
        if(isCleanSession) b1 = b1 or 0x02
        // MQTT v3.1.1: The Server MUST validate that the reserved flag in the CONNECT Control Packet is
        // set to zero and disconnect the Client if it is not zero.
        // See https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349230
        if (version == MqttVersion.MQTT_3_1_1.protocolVersion() || version == MqttVersion.MQTT_5.protocolVersion())
            b1 = b1 and 0xFE
        byteArray += byteArrayOf(b1.toByte())
        byteArray += keepAliveTimeSeconds.toUShort().toDecByteArray()
        byteArray += properties.toDecByteArray()
        return byteArray
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName ?: "null object")
            .append('[')
            .append("name=").append(name)
            .append(", version=").append(version)
            .append(", hasUserName=").append(hasUserName)
            .append(", hasPassword=").append(hasPassword)
            .append(", isWillRetain=").append(isWillRetain)
            .append(", isWillFlag=").append(isWillFlag)
            .append(", isCleanSession=").append(isCleanSession)
            .append(", keepAliveTimeSeconds=").append(keepAliveTimeSeconds)
            .append(']')
            .toString()
    }

}
