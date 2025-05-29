package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttProperties.Companion.withEmptyDefaults
import io.zibi.codec.mqtt.util.toDecByteArray
import java.util.Arrays
import kotlin.text.Charsets.UTF_8

/**
 * Payload of [MqttConnectMessage]
 */
class MqttConnectPayload(
    private val clientIdentifier: String?,
    willProperties: MqttProperties?,
    private val willTopic: String?,
    private val willMessage: ByteArray?,
    private val userName: String?,
    private val password: ByteArray?
) {
    private val willProperties: MqttProperties

    @Deprecated(
        """use {@link MqttConnectPayload#MqttConnectPayload(String,
     * MqttProperties, String, byte[], String, byte[])} instead"""
    )
    constructor(
        clientIdentifier: String?,
        willTopic: String?,
        willMessage: String,
        userName: String?,
        password: String
    ) : this(
        clientIdentifier,
        MqttProperties.NO_PROPERTIES,
        willTopic,
        willMessage.toByteArray(UTF_8),
        userName,
        password.toByteArray(UTF_8)
    )

    constructor(
        clientIdentifier: String?,
        willTopic: String?,
        willMessage: ByteArray?,
        userName: String?,
        password: ByteArray?
    ) : this(
        clientIdentifier,
        MqttProperties.NO_PROPERTIES,
        willTopic,
        willMessage,
        userName,
        password
    )

    init {
        this.willProperties = withEmptyDefaults(willProperties)
    }

    fun clientIdentifier(): String? {
        return clientIdentifier
    }

    fun willProperties(): MqttProperties {
        return willProperties
    }

    fun willTopic(): String? {
        return willTopic
    }

    @Deprecated("use {@link MqttConnectPayload#willMessageInBytes()} instead")
    fun willMessage(): String? {
        return if (willMessage == null) null else String(willMessage, UTF_8)
    }

    fun willMessageInBytes(): ByteArray? {
        return willMessage
    }

    fun userName(): String? {
        return userName
    }

    @Deprecated("use {@link MqttConnectPayload#passwordInBytes()} instead")
    fun password(): String? {
        return if (password == null) null else String(password, UTF_8)
    }

    fun passwordInBytes(): ByteArray? {
        return password
    }

    fun toDecByteArray(variableHeader: MqttConnectVariableHeader): ByteArray{
        var byteArray = byteArrayOf()
        byteArray += clientIdentifier.toDecByteArray()
        if (variableHeader.isWillFlag) {
            byteArray += if (variableHeader.version == MqttVersion.MQTT_5.protocolVersion()) {
                willProperties.toDecByteArray()
            } else {
                byteArrayOf()
            }
            byteArray += willTopic.toDecByteArray()
            byteArray += willMessage.toDecByteArray()
        }
        if (variableHeader.hasUserName)
            byteArray += userName.toDecByteArray()
        if (variableHeader.hasPassword)
            byteArray += password.toDecByteArray()
        return byteArray
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("clientIdentifier=").append(clientIdentifier)
            .append(", willTopic=").append(willTopic)
            .append(", willMessage=").append(Arrays.toString(willMessage))
            .append(", userName=").append(userName)
            .append(", password=").append(Arrays.toString(password))
            .append(']')
            .toString()
    }
}
