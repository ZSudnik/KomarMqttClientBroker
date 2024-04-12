package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.MqttConstant.DEFAULT_MAX_CLIENT_ID_LENGTH
import io.zibi.codec.mqtt.MqttVersion.Companion.fromProtocolNameAndLevel
import io.zibi.codec.mqtt.exception.EncoderException
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.PAYLOAD_FORMAT_INDICATOR
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.AUTHENTICATION_DATA
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.CONTENT_TYPE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.REQUEST_PROBLEM_INFORMATION
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.MAXIMUM_PACKET_SIZE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.MAXIMUM_QOS
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.REASON_STRING
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.RECEIVE_MAXIMUM
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.WILL_DELAY_INTERVAL
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.USER_PROPERTY
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.TOPIC_ALIAS_MAXIMUM
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.REQUEST_RESPONSE_INFORMATION
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.RETAIN_AVAILABLE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.CORRELATION_DATA
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.RESPONSE_TOPIC
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.TOPIC_ALIAS
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.RESPONSE_INFORMATION
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.SERVER_REFERENCE
import io.ktor.utils.io.bits.highByte
import io.ktor.utils.io.bits.highShort
import io.ktor.utils.io.bits.lowByte
import io.ktor.utils.io.bits.lowShort
import kotlin.text.Charsets.UTF_8

/**
 * Encodes Mqtt messages into bytes following the protocol specification v3.1
 * as described here [MQTTV3.1](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html)
 * or v5.0 as described here [MQTTv5.0](https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html) -
 * depending on the version specified in the first CONNECT message that goes through the channel.
 */
//@ChannelHandler.Sharable
object MqttEncoder  {//}: MessageToMessageEncoder<MqttMessage>() {
    @Throws(Exception::class)
    fun encode(ctx: MqttVersion, msg: MqttMessage, out: MutableList<Any>) {
        out.add(doEncode(ctx, msg))
    }

    /**
     * This is the main encoding method.
     * It's only visible for testing.
     *
     * @param message MQTT message to encode
     * @return ByteBuf with encoded bytes
     */
    fun doEncode(
        mqttVersion: MqttVersion,
        message: MqttMessage
    ): MutableList<Byte> {
        return when (message.fixedHeader.messageType) {
            MqttMessageType.CONNECT -> encodeConnectMessage(
                message as MqttConnectMessage
            )
            MqttMessageType.CONNACK -> encodeConnAckMessage(
                mqttVersion,
                message as MqttConnAckMessage
            )
            MqttMessageType.PUBLISH -> encodePublishMessage(
                mqttVersion,
                message as MqttPublishMessage
            )
            MqttMessageType.SUBSCRIBE -> encodeSubscribeMessage(
                mqttVersion,
                message as MqttSubscribeMessage
            )
            MqttMessageType.UNSUBSCRIBE -> encodeUnsubscribeMessage(
                mqttVersion,
                message as MqttUnsubscribeMessage
            )
            MqttMessageType.SUBACK -> encodeSubAckMessage(
                mqttVersion,
                message as MqttSubAckMessage
            )
            MqttMessageType.UNSUBACK -> {
                if (message is MqttUnsubAckMessage) {
                    encodeUnsubAckMessage(
                        mqttVersion,
                        message
                    )
                } else encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(
                    message
                )
            }
            MqttMessageType.PUBACK, MqttMessageType.PUBREC, MqttMessageType.PUBREL, MqttMessageType.PUBCOMP -> encodePubReplyMessage(
                mqttVersion,
                message
            )
            MqttMessageType.DISCONNECT, MqttMessageType.AUTH -> encodeReasonCodePlusPropertiesMessage(
                mqttVersion,
                message
            )
            MqttMessageType.PINGREQ, MqttMessageType.PINGRESP -> encodeMessageWithOnlySingleByteFixedHeader(
                message
            )
//            else -> throw IllegalArgumentException("Unknown message type: " + message.fixedHeader.messageType.value())
        }
    }

    private fun encodeConnectMessage(
        message: MqttConnectMessage
    ): MutableList<Byte> {
        var payloadBufferSize = 0
        val mqttFixedHeader = message.fixedHeader
        val variableHeader = message.variableHeader()
        val payload = message.payload()
        val mqttVersion =
            fromProtocolNameAndLevel(variableHeader.name, variableHeader.version.toByte())

        // as MQTT 3.1 & 3.1.1 spec, If the User Name Flag is set to 0, the Password Flag MUST be set to 0
        if (!variableHeader.hasUserName && variableHeader.hasPassword) {
            throw EncoderException("Without a username, the password MUST be not set")
        }

        // Client id
        val clientIdentifier = payload.clientIdentifier()
        if (!isValidClientId(mqttVersion, DEFAULT_MAX_CLIENT_ID_LENGTH, clientIdentifier)) {
            throw MqttIdentifierRejectedException("invalid clientIdentifier: $clientIdentifier")
        }
        val clientIdentifierBytes = clientIdentifier?.length ?: 0
        payloadBufferSize += 2 + clientIdentifierBytes

        // Will topic and message
        val willTopic = payload.willTopic()
        val willTopicBytes = nullableUtf8Bytes(willTopic)
        val willMessage = payload.willMessageInBytes()
        val willMessageBytes = willMessage ?: byteArrayOf()
        if (variableHeader.isWillFlag) {
            payloadBufferSize += 2 + willTopicBytes
            payloadBufferSize += 2 + willMessageBytes.size
        }
        val userName = payload.userName()
        val userNameBytes = nullableUtf8Bytes(userName)
        if (variableHeader.hasUserName) {
            payloadBufferSize += 2 + userNameBytes
        }
        val password = payload.passwordInBytes()
        val passwordBytes = password ?: byteArrayOf()
        if (variableHeader.hasPassword) {
            payloadBufferSize += 2 + passwordBytes.size
        }

        // Fixed and variable header
        val protocolNameBytes = mqttVersion.protocolNameBytes()
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
            val willPropertiesBuf: MutableList<Byte>
            if (variableHeader.isWillFlag) {
                willPropertiesBuf =
                    encodePropertiesIfNeeded(
                        mqttVersion,
                        payload.willProperties()
                    )
                payloadBufferSize += willPropertiesBuf.size
            } else {
                willPropertiesBuf = emptyList<Byte>().toMutableList()
            }
            try {
                val variableHeaderBufferSize = 2 + protocolNameBytes.size + 4 + propertiesBuf.size
                val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//                val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//                val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
                val buf: MutableList<Byte> = mutableListOf()
                buf.writeByte(getFixedHeaderByte1(mqttFixedHeader))
                writeVariableLengthInt(buf, variablePartSize)
                buf.writeShort(protocolNameBytes.size)
                buf.writeBytes(protocolNameBytes)
                buf.writeByte(variableHeader.version.toByte())
                buf.writeByte(getConnVariableHeaderFlag(variableHeader))
                buf.writeShort(variableHeader.keepAliveTimeSeconds)
                buf.writeBytes(propertiesBuf)

                // Payload
                writeExactUTF8String(
                    buf,
                    clientIdentifier,
                    clientIdentifierBytes
                )
                if (variableHeader.isWillFlag) {
                    buf.writeBytes(willPropertiesBuf)
                    writeExactUTF8String(buf, willTopic, willTopicBytes)
                    buf.writeShort(willMessageBytes.size)
                    buf.writeBytes(willMessageBytes)
                }
                if (variableHeader.hasUserName) {
                    writeExactUTF8String(buf, userName, userNameBytes)
                }
                if (variableHeader.hasPassword) {
                    buf.writeShort(passwordBytes.size)
                    buf.writeBytes(passwordBytes)
                }
                buf
            } finally {
                willPropertiesBuf.clear()
            }
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun getConnVariableHeaderFlag(variableHeader: MqttConnectVariableHeader): Byte {
        var flagByte = 0
        if (variableHeader.hasUserName) {
            flagByte = flagByte or 0x80
        }
        if (variableHeader.hasPassword) {
            flagByte = flagByte or 0x40
        }
        if (variableHeader.isWillRetain) {
            flagByte = flagByte or 0x20
        }
        flagByte = flagByte or (variableHeader.willQos and 0x03 shl 3)
        if (variableHeader.isWillFlag) {
            flagByte = flagByte or 0x04
        }
        if (variableHeader.isCleanSession) {
            flagByte = flagByte or 0x02
        }
        return flagByte.toByte()
    }

    private fun encodeConnAckMessage(
        mqttVersion: MqttVersion,
        message: MqttConnAckMessage
    ): MutableList<Byte> {
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
//            val buf = ctx.alloc().buffer(4 + propertiesBuf.readableBytes())
            val buf = mutableListOf<Byte>()
            buf.writeByte(getFixedHeaderByte1(message.fixedHeader))
            writeVariableLengthInt(buf, 2 + propertiesBuf.size)
            buf.writeByte(if (message.variableHeader().isSessionPresent) 0x01 else 0x00)
            buf.writeByte(message.variableHeader().connectReturnCode().byteValue())
            buf.writeBytes(propertiesBuf)
            buf
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun encodeSubscribeMessage(
        mqttVersion: MqttVersion,
        message: MqttSubscribeMessage
    ): MutableList<Byte> {
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
            val variableHeaderBufferSize = 2 + propertiesBuf.size
            var payloadBufferSize = 0
            val mqttFixedHeader = message.fixedHeader
            val variableHeader = message.variableHeader()
            val payload = message.payload()
            for (topic in payload.topicSubscriptions) {
                val topicName = topic.topicFilter
                val topicNameBytes = topicName.length
                payloadBufferSize += 2 + topicNameBytes
                payloadBufferSize += 1
            }
            val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//            val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//            val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
            val buf = mutableListOf<Byte>()
            buf.writeByte(getFixedHeaderByte1(mqttFixedHeader))
            writeVariableLengthInt(buf, variablePartSize)

            // Variable Header
            val messageId = variableHeader.messageId
            buf.writeShort(messageId)
            buf.writeBytes(propertiesBuf)

            // Payload
            for (topic in payload.topicSubscriptions) {
                writeUnsafeUTF8String(buf, topic.topicFilter)
                if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_3_1) {
                    buf.writeByte(topic.qualityOfService().value().toByte())
                } else {
                    val option = topic.option
                    var optionEncoded = option.retainHandling.value() shl 4
                    if (option.isRetainAsPublished) {
                        optionEncoded = optionEncoded or 0x08
                    }
                    if (option.isNoLocal) {
                        optionEncoded = optionEncoded or 0x04
                    }
                    optionEncoded = optionEncoded or option.qos.value()
                    buf.writeByte(optionEncoded.toByte())
                }
            }
            buf
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun encodeUnsubscribeMessage(
        mqttVersion: MqttVersion,
        message: MqttUnsubscribeMessage
    ): MutableList<Byte> {
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
            val variableHeaderBufferSize = 2 + propertiesBuf.size
            var payloadBufferSize = 0
            val mqttFixedHeader = message.fixedHeader
            val variableHeader = message.variableHeader()
            val payload = message.payload()
            for (topicName in payload.topics) {
                val topicNameBytes = topicName.length
                payloadBufferSize += 2 + topicNameBytes
            }
            val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//            val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//            val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
            val buf = mutableListOf<Byte>()
            buf.writeByte(getFixedHeaderByte1(mqttFixedHeader))
            writeVariableLengthInt(buf, variablePartSize)

            // Variable Header
            val messageId = variableHeader.messageId
            buf.writeShort(messageId)
            buf.writeBytes(propertiesBuf)

            // Payload
            for (topicName in payload.topics) {
                writeUnsafeUTF8String(buf, topicName)
            }
            buf
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun encodeSubAckMessage(
        mqttVersion: MqttVersion,
        message: MqttSubAckMessage
    ): MutableList<Byte> {
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
            val variableHeaderBufferSize = 2 + propertiesBuf.size
            val payloadBufferSize = message.payload().grantedQoSLevels().size
            val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//            val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//            val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
            val buf = mutableListOf<Byte>()
            buf.writeByte(getFixedHeaderByte1(message.fixedHeader))
            writeVariableLengthInt(buf, variablePartSize)
            buf.writeShort(message.variableHeader().messageId)
            buf.writeBytes(propertiesBuf)
            for (code in message.payload().reasonCodes()) {
                buf.writeByte(code.toByte())
            }
            buf
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun encodeUnsubAckMessage(
        mqttVersion: MqttVersion,
        message: MqttUnsubAckMessage
    ): MutableList<Byte> {
        return if (message.variableHeader() is MqttMessageVariableHeader) {
            val propertiesBuf = encodePropertiesIfNeeded(
                mqttVersion,
                message.variableHeader().properties()
            )
            try {
                val variableHeaderBufferSize = 2 + propertiesBuf.size
                val payload = message.payload()
                val payloadBufferSize = payload.unsubscribeReasonCodes().size
                val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//                val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//                val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
                val buf = mutableListOf<Byte>()
                buf.writeByte(getFixedHeaderByte1(message.fixedHeader))
                writeVariableLengthInt(buf, variablePartSize)
                buf.writeShort(message.variableHeader().messageId)
                buf.writeBytes(propertiesBuf)
//                if (payload != null) {
                    for (reasonCode in payload.unsubscribeReasonCodes()) {
                        buf.writeByte(reasonCode.toByte())
                    }
//                }
                buf
            } finally {
                propertiesBuf.clear()
            }
        } else {
            encodeMessageWithOnlySingleByteFixedHeaderAndMessageId( message)
        }
    }

    private fun encodePublishMessage(
        mqttVersion: MqttVersion,
        message: MqttPublishMessage
    ): MutableList<Byte> {
        val mqttFixedHeader = message.fixedHeader
        val variableHeader = message.variableHeader()
        val payload = message.payload().clone()
        val topicName = variableHeader.topicName
        val topicNameBytes = topicName.length
        val propertiesBuf = encodePropertiesIfNeeded(
            mqttVersion,
            message.variableHeader().properties()
        )
        return try {
            val variableHeaderBufferSize = 2 + topicNameBytes +
                    (if (mqttFixedHeader.qosLevel
                            .value() > 0
                    ) 2 else 0) + propertiesBuf.size
            val payloadBufferSize = payload.size
            val variablePartSize = variableHeaderBufferSize + payloadBufferSize
//            val fixedHeaderBufferSize = 1 + getVariableLengthInt(variablePartSize)
//            val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variablePartSize)
            val buf = mutableListOf<Byte>()
            buf.writeByte(
                getFixedHeaderByte1(
                    mqttFixedHeader
                )
            )
            writeVariableLengthInt(
                buf,
                variablePartSize
            )
            writeExactUTF8String(
                buf,
                topicName,
                topicNameBytes
            )
            if (mqttFixedHeader.qosLevel.value() > 0) {
                buf.writeShort(variableHeader.packetId)
            }
            buf.writeBytes(propertiesBuf)
            buf.writeBytes(payload)
            buf
        } finally {
            propertiesBuf.clear()
        }
    }

    private fun encodePubReplyMessage(
        mqttVersion: MqttVersion,
        message: MqttMessage
    ): MutableList<Byte> {
        return if (message.variableHeader() is MqttPubReplyMessageVariableHeader) {
            val mqttFixedHeader = message.fixedHeader
            val variableHeader =
                message.variableHeader() as MqttPubReplyMessageVariableHeader?
            val msgId = variableHeader!!.messageId
            val propertiesBuf: MutableList<Byte>
            val includeReasonCode: Boolean
            val variableHeaderBufferSize: Int
            if (mqttVersion == MqttVersion.MQTT_5 &&
                (variableHeader.reasonCode != MqttPubReplyMessageVariableHeader.REASON_CODE_OK ||
                        !variableHeader.properties().isEmpty)
            ) {
                propertiesBuf = encodeProperties(
                    variableHeader.properties()
                )
                includeReasonCode = true
                variableHeaderBufferSize = 3 + propertiesBuf.size
            } else {
                propertiesBuf = mutableListOf()
                includeReasonCode = false
                variableHeaderBufferSize = 2
            }
            try {
//                val fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize)
//                val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variableHeaderBufferSize)
                val buf = mutableListOf<Byte>()
                buf.writeByte(
                    getFixedHeaderByte1(
                        mqttFixedHeader
                    )
                )
                writeVariableLengthInt(
                    buf,
                    variableHeaderBufferSize
                )
                buf.writeShort(msgId)
                if (includeReasonCode) {
                    buf.writeByte(variableHeader.reasonCode)
                }
                buf.writeBytes(propertiesBuf)
                buf
            } finally {
                propertiesBuf.clear()
            }
        } else {
            encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(
                message
            )
        }
    }

    private fun encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(
        message: MqttMessage
    ): MutableList<Byte> {
        val mqttFixedHeader = message.fixedHeader
        val variableHeader = message.variableHeader() as MqttMessageVariableHeader?
        val msgId = variableHeader!!.messageId
        val variableHeaderBufferSize = 2 // variable part only has a message id
//        val fixedHeaderBufferSize = 1 + getVariableLengthInt(variableHeaderBufferSize)
//        val buf = byteBufAllocator.buffer(fixedHeaderBufferSize + variableHeaderBufferSize)
        val buf = mutableListOf<Byte>()
        buf.writeByte(getFixedHeaderByte1(mqttFixedHeader))
        writeVariableLengthInt(buf, variableHeaderBufferSize)
        buf.writeShort(msgId.toShort())
        return buf
    }

    private fun encodeReasonCodePlusPropertiesMessage(
        mqttVersion: MqttVersion,
        message: MqttMessage
    ): MutableList<Byte> {
        return if (message.variableHeader() is MqttReasonCodeAndPropertiesVariableHeader) {
            val mqttFixedHeader = message.fixedHeader
            val variableHeader =
                message.variableHeader() as MqttReasonCodeAndPropertiesVariableHeader?
            val propertiesBuf: MutableList<Byte>
            val includeReasonCode: Boolean
            val variableHeaderBufferSize: Int
            if (mqttVersion == MqttVersion.MQTT_5 &&
                (variableHeader!!.reasonCode != MqttReasonCodeAndPropertiesVariableHeader.REASON_CODE_OK ||
                        !variableHeader.properties().isEmpty)
            ) {
                propertiesBuf = encodeProperties(
                    variableHeader.properties()
                )
                includeReasonCode = true
                variableHeaderBufferSize = 1 + propertiesBuf.size
            } else {
                propertiesBuf = emptyList<Byte>().toMutableList()
                includeReasonCode = false
                variableHeaderBufferSize = 0
            }
            try {
                val fixedHeaderBufferSize =
                    1 + getVariableLengthInt(
                        variableHeaderBufferSize
                    )
//                val buf = ctx.alloc().buffer(fixedHeaderBufferSize + variableHeaderBufferSize)
                val buf = mutableListOf<Byte>()
                buf.writeByte(
                    getFixedHeaderByte1(
                        mqttFixedHeader
                    )
                )
                writeVariableLengthInt(
                    buf,
                    variableHeaderBufferSize
                )
                if (includeReasonCode) {
                    buf.writeByte(variableHeader!!.reasonCode)
                }
                buf.writeBytes(propertiesBuf)
                buf
            } finally {
                propertiesBuf.clear()
            }
        } else {
            encodeMessageWithOnlySingleByteFixedHeader(
                message
            )
        }
    }

    private fun encodeMessageWithOnlySingleByteFixedHeader(
        message: MqttMessage
    ): MutableList<Byte> {
        val mqttFixedHeader = message.fixedHeader
        val buf = mutableListOf<Byte>()
        buf.writeByte(getFixedHeaderByte1(mqttFixedHeader))
        buf.writeByte(0)
        return buf
    }

    private fun encodePropertiesIfNeeded(
        mqttVersion: MqttVersion,
        mqttProperties: MqttProperties
    ): MutableList<Byte> {
        return if (mqttVersion == MqttVersion.MQTT_5) {
            encodeProperties(
                mqttProperties
            )
        } else emptyList<Byte>().toMutableList()
    }

    private fun encodeProperties(
        mqttProperties: MqttProperties
    ): MutableList<Byte> {
        val propertiesHeaderBuf = mutableListOf<Byte>()
        // encode also the Properties part
        return try {
            val propertiesBuf = mutableListOf<Byte>()
            try {
                for (property in mqttProperties.listAll()) {
                    when (val propertyType = MqttPropertyType.valueOf(property.propertyId)) {
                        PAYLOAD_FORMAT_INDICATOR, REQUEST_PROBLEM_INFORMATION, REQUEST_RESPONSE_INFORMATION, MAXIMUM_QOS, RETAIN_AVAILABLE, WILDCARD_SUBSCRIPTION_AVAILABLE, SUBSCRIPTION_IDENTIFIER_AVAILABLE, SHARED_SUBSCRIPTION_AVAILABLE -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            val bytePropValue = (property as MqttProperties.IntegerProperty?)!!.value.toByte()
                            propertiesBuf.writeByte(bytePropValue)
                        }

                        SERVER_KEEP_ALIVE, RECEIVE_MAXIMUM, TOPIC_ALIAS_MAXIMUM, TOPIC_ALIAS -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            val twoBytesInPropValue = (property as MqttProperties.IntegerProperty?)!!.value.toShort()
                            propertiesBuf.writeShort(twoBytesInPropValue)
                        }

                        PUBLICATION_EXPIRY_INTERVAL, SESSION_EXPIRY_INTERVAL, WILL_DELAY_INTERVAL, MAXIMUM_PACKET_SIZE -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            val fourBytesIntPropValue = (property as MqttProperties.IntegerProperty?)!!.value
                            propertiesBuf.writeInt(fourBytesIntPropValue)
                        }

                        SUBSCRIPTION_IDENTIFIER -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            val vbi = (property as MqttProperties.IntegerProperty?)!!.value
                            writeVariableLengthInt(propertiesBuf, vbi)
                        }

                        CONTENT_TYPE, RESPONSE_TOPIC, ASSIGNED_CLIENT_IDENTIFIER, AUTHENTICATION_METHOD, RESPONSE_INFORMATION, SERVER_REFERENCE, REASON_STRING -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            writeEagerUTF8String(
                                propertiesBuf, (property as MqttProperties.StringProperty?)!!.value
                            )
                        }

                        USER_PROPERTY -> {
                            val pairs = (property as MqttProperties.UserProperties?)!!.value
                            for (pair in pairs) {
                                writeVariableLengthInt(propertiesBuf, property.propertyId)
                                writeEagerUTF8String(propertiesBuf, pair.key)
                                writeEagerUTF8String(propertiesBuf, pair.value)
                            }
                        }

                        CORRELATION_DATA, AUTHENTICATION_DATA -> {
                            writeVariableLengthInt(propertiesBuf, property.propertyId)
                            val binaryPropValue =
                                (property as MqttProperties.BinaryProperty?)!!.value
                            propertiesBuf.writeShort(binaryPropValue.size.toShort())
                            propertiesBuf.writeBytes(binaryPropValue)
                        }

                        else ->                             //shouldn't reach here
                            throw EncoderException("Unknown property type: $propertyType")
                    }
                }
                writeVariableLengthInt(propertiesHeaderBuf, propertiesBuf.size)
                propertiesHeaderBuf.writeBytes(propertiesBuf)
                propertiesHeaderBuf
            } finally {
                propertiesBuf.clear()
            }
        } catch (e: RuntimeException) {
            propertiesHeaderBuf.clear()
            throw e
        }
    }

    private fun getFixedHeaderByte1(header: MqttFixedHeader?): Byte {
        var ret = 0
        ret = ret or (header!!.messageType.value() shl 4)
        if (header.isDup) {
            ret = ret or 0x08
        }
        ret = ret or (header.qosLevel.value() shl 1)
        if (header.isRetain) {
            ret = ret or 0x01
        }
        return ret.toByte()
    }

    private fun writeVariableLengthInt(buf: MutableList<Byte>, numInt: Int) {
        var num = numInt
        do {
            var digit = num % 128
            num /= 128
            if (num > 0) {
                digit = digit or 0x80
            }
            buf.add(digit.toByte())
        } while (num > 0)
    }

    private  fun nullableUtf8Bytes(s: String?): Int {
        return s?.toByteArray(UTF_8)?.size ?: 0
    }

    private fun nullableMaxUtf8Bytes(s: String?): Int {
        return s?.toByteArray(UTF_8)?.size ?: 0
//        return if (s == null) 0 else ByteBufUtil.utf8MaxBytes(s)
    }

    private fun writeExactUTF8String(buf: MutableList<Byte>, s: String?, utf8Length: Int) {
//        buf.ensureWritable(utf8Length + 2)
        buf.writeShort(utf8Length.toShort())
//        buf.writeShort(utf8Length.toShort())
        if (utf8Length > 0 && s != null) {
            buf.writeBytes( s.toByteArray(UTF_8))
//            val writtenUtf8Length = ByteBufUtil.reserveAndWriteUtf8(buf, s, utf8Length)
//            assert(writtenUtf8Length == utf8Length)
        }
    }


    private fun writeEagerUTF8String(buf: MutableList<Byte>, s: String?) {
        val maxUtf8Length = nullableMaxUtf8Bytes(s)
        buf.writeShort( maxUtf8Length )
        s?.let {
            buf.writeBytes(it.toByteArray(UTF_8))
        }
//        buf.ensureWritable(maxUtf8Length + 2)
//        val writerIndex = buf.writerIndex()
//        val startUtf8String = writerIndex + 2
//        buf.writerIndex(startUtf8String)
//        val utf8Length =
//            if (s != null) ByteBufUtil.reserveAndWriteUtf8(buf, s, maxUtf8Length) else 0
//        buf.setShort(writerIndex, utf8Length)
    }

    private fun writeUnsafeUTF8String(buf: MutableList<Byte>, s: String?) {
        val utf8Length = nullableUtf8Bytes(s)
        buf.writeShort( utf8Length )
        s?.let {
            buf.writeBytes(it.toByteArray(UTF_8))
        }
    //        val writerIndex = buf.writerIndex()
//        val startUtf8String = writerIndex + 2
        // no need to reserve any capacity here, already done earlier: that's why is Unsafe
//        buf.writerIndex(startUtf8String)
//        val utf8Length = if (s != null) ByteBufUtil.reserveAndWriteUtf8(buf, s, 0) else 0
//        buf.setShort(writerIndex, utf8Length)
    }

    private fun getVariableLengthInt(numInt: Int): Int {
        var num = numInt
        var count = 0
        do {
            num /= 128
            count++
        } while (num > 0)
        return count
    }


    private fun MutableList<Byte>.writeInt(value: Int){
        val up = value.highShort
        this.add(up.highByte)
        this.add(up.lowByte)
        val down = value.lowShort
        this.add(down.highByte)
        this.add(down.lowByte)
    }

    private fun MutableList<Byte>.writeShort(value: Int){
        this.writeShort( value.toShort())
    }
    private fun MutableList<Byte>.writeShort(value: Short){
        this.add(value.highByte)
        this.add(value.lowByte)
    }

    private fun MutableList<Byte>.writeByte(value: Byte){
        this.add(value)
    }

    private fun MutableList<Byte>.writeBytes(value: MutableList<Byte>){
        this.addAll(value)
    }
    private fun MutableList<Byte>.writeBytes(value: ByteArray){
        this.addAll(value.toList())
    }

    private fun isValidClientId(
        mqttVersion: MqttVersion,
        maxClientIdLength: Int,
        clientId: String?
    ): Boolean {
        if (mqttVersion == MqttVersion.MQTT_3_1) {
            return clientId != null && clientId.length >= MqttConstant.MIN_CLIENT_ID_LENGTH && clientId.length <= maxClientIdLength
        }
        if (mqttVersion == MqttVersion.MQTT_3_1_1 || mqttVersion == MqttVersion.MQTT_5) {
            // In 3.1.3.1 Client Identifier of MQTT 3.1.1 and 5.0 specifications, The Server MAY allow ClientId’s
            // that contain more than 23 encoded bytes. And, The Server MAY allow zero-length ClientId.
            return clientId != null
        }
        throw IllegalArgumentException("$mqttVersion is unknown mqtt version")
    }
}

