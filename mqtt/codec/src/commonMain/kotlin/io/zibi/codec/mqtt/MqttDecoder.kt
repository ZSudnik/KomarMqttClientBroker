package io.zibi.codec.mqtt

import io.ktor.utils.io.core.internal.ChunkBuffer
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.readInt
import io.ktor.utils.io.core.readUByte
import io.zibi.codec.mqtt.MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE
import io.zibi.codec.mqtt.MqttConstant.DEFAULT_MAX_CLIENT_ID_LENGTH
import io.zibi.codec.mqtt.MqttMessageType.*
import io.zibi.codec.mqtt.MqttProperties.MqttPropertyType.*
import io.zibi.codec.mqtt.MqttVersion.Companion.fromProtocolNameAndLevel
import io.zibi.codec.mqtt.exception.DecoderException
import io.zibi.codec.mqtt.reasoncode.Disconnect
import io.zibi.codec.mqtt.reasoncode.PubComp
import io.zibi.codec.mqtt.reasoncode.ReasonCode
import kotlin.text.Charsets.UTF_8

/**
 * Decodes Mqtt messages from bytes, following
 * the MQTT protocol specification
 * [v3.1](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html)
 * or
 * [v5.0](https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html), depending on the
 * version specified in the CONNECT message that first goes through the channel.
 */
class MqttDecoder(
    val maxBytesInMessage: Int = DEFAULT_MAX_BYTES_IN_MESSAGE,
    private val maxClientIdLength: Int = DEFAULT_MAX_CLIENT_ID_LENGTH
) {


    private var mqttFixedHeader: MqttFixedHeader? = null

    init {
        if (maxBytesInMessage <= 0) throw IllegalArgumentException("maxBytesInMessage : $maxBytesInMessage (expected: > 0)")
        if (maxClientIdLength <= 0) throw IllegalArgumentException("maxClientIdLength : $maxClientIdLength (expected: > 0)")
    }

    /**
     * Decodes the variable header (if any)
     * @param buffer the buffer to decode from
     * @param mqttFixedHeader MqttFixedHeader of the same message
     * @return the variable header
     */
    private fun decodeVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer,
        mqttFixedHeader: MqttFixedHeader?
    ): Any? {
        return when (mqttFixedHeader!!.messageType) {
            CONNECT -> decodeConnectionVariableHeader(
                buffer
            )

            CONNACK -> decodeConnAckVariableHeader(
                mqttVersion,
                buffer
            )

            UNSUBSCRIBE, SUBSCRIBE, SUBACK, UNSUBACK -> decodeVariableHeader(
                mqttVersion,
                buffer
            )

            PUBACK, PUBREC, PUBCOMP, PUBREL -> decodePubReplyMessage(
                mqttVersion,
                buffer
            )

            PUBLISH -> decodePublishVariableHeader(
                mqttVersion,
                buffer,
                mqttFixedHeader
            )

            DISCONNECT, AUTH -> decodeReasonCodeAndPropertiesVariableHeader(
                mqttVersion,
                buffer
            )

            PINGREQ, PINGRESP ->                 // Empty variable header
                null
        }
    }

    private fun decodePubReplyMessage(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer
    ): MqttPubReplyMessageVariableHeader {
        val packetId = decodeMessageId(buffer)
        return if ( mqttFixedHeader!!.remainingLength - buffer.readPosition > 3 ){
            val reasonCode = buffer.readByte()
            val properties = decodeProperties(buffer, mqttVersion)
            MqttPubReplyMessageVariableHeader(
                packetId,
                reasonCode,
                properties
            )
        } else if (mqttFixedHeader!!.remainingLength - buffer.readPosition > 2 ){
            val reasonCode = buffer.readByte()
            MqttPubReplyMessageVariableHeader(
                packetId,
                reasonCode,
                MqttProperties.NO_PROPERTIES
            )
        } else {
            MqttPubReplyMessageVariableHeader(
                packetId, 0.toByte(),
                MqttProperties.NO_PROPERTIES
            )
        }

    }

    private fun decodeReasonCodeAndPropertiesVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer
    ): MqttReasonCodeAndPropertiesVariableHeader {
        val reasonCode: Byte
        val properties: MqttProperties
        if (mqttFixedHeader!!.remainingLength - buffer.readPosition > 1) {
            reasonCode = buffer.readByte()
            properties = decodeProperties(buffer, mqttVersion)
        } else if (mqttFixedHeader!!.remainingLength - buffer.readPosition > 0) {
            reasonCode = buffer.readByte()
            properties = MqttProperties.NO_PROPERTIES
        } else {
            reasonCode = 0
            properties = MqttProperties.NO_PROPERTIES
        }
        return  MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties)
    }

    fun decodePublishVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer,
        mqttFixedHeader: MqttFixedHeader?
    ): MqttPublishVariableHeader {
        val decodedTopic = decodeString(buffer)
        if (!isValidPublishTopicName(decodedTopic!!)) {
            throw DecoderException("Invalid publish topic name: $decodedTopic (contains wildcards)")
        }
        var messageId = -1
        if (mqttFixedHeader!!.qosLevel.value() > 0) {
            messageId = decodeMessageId(buffer)
        }
        val properties: MqttProperties = decodeProperties(buffer, mqttVersion)
        return MqttPublishVariableHeader(decodedTopic, messageId, properties)
    }

    /**
     * Decodes the fixed header. It's one byte for the flags and then variable
     * bytes for the remaining length.
     * https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/errata01/os/mqtt-v3.1.1-errata01-os-complete.html._Toc442180841
     *
     * @param buffer the buffer to decode from
     * @return the fixed header
     */
    suspend fun decodeFixedHeader(
        mqttVersion: MqttVersion,
        readChannel: suspend (Int) -> ByteArray,
    ): MqttFixedHeader {
        val b1 = readChannel(1).first().toUByte().toInt()
        val messageType = MqttMessageType.valueOf(b1 shr 4)
        val dupFlag = b1 and 0x08 == 0x08
        val qosLevel = b1 and 0x06 shr 1
        val retain = b1 and 0x01 != 0
        when (messageType) {
            PUBLISH -> if (qosLevel == 3) {
                throw DecoderException("Illegal QOS Level in fixed header of PUBLISH message ($qosLevel)")
            }
            PUBREL, SUBSCRIBE, UNSUBSCRIBE -> {
                if (dupFlag)
                    throw DecoderException("Illegal BIT 3 in fixed header of $messageType message, must be 0, found 1")
                if (qosLevel != 1)
                    throw DecoderException("Illegal QOS Level in fixed header of $messageType message, must be 1, found $qosLevel")
                if (retain)
                    throw DecoderException("Illegal BIT 0 in fixed header of $messageType message, must be 0, found 1")
            }
            AUTH, CONNACK, CONNECT, DISCONNECT, PINGREQ, PINGRESP, PUBACK, PUBCOMP, PUBREC, SUBACK, UNSUBACK -> {
                if (dupFlag)
                    throw DecoderException("Illegal BIT 3 in fixed header of $messageType message, must be 0, found 1")
                if (qosLevel != 0)
                    throw DecoderException("Illegal BIT 2 or 1 in fixed header of $messageType message, must be 0, found $qosLevel")
                if (retain)
                    throw DecoderException("Illegal BIT 0 in fixed header of $messageType message, must be 0, found 1")
            }
            else -> {
                throw DecoderException("Unknown message type, do not know how to validate fixed header")
            }
        }
        val remainingLength = decodeVariableByteInteger(readChannel,messageType)
        mqttFixedHeader = validateFixedHeader(mqttVersion, resetUnusedFields(
            MqttFixedHeader(messageType, dupFlag, MqttQoS.valueOf(qosLevel), retain, remainingLength)
        ))
        return mqttFixedHeader!!
    }

    fun decodeConnectionVariableHeader(
        buffer: ChunkBuffer
    ): MqttConnectVariableHeader {
        val protoString = decodeString(buffer)
        val protocolLevel = buffer.readByte()
        val version = fromProtocolNameAndLevel(
            protoString!!, protocolLevel
        )
        val b1 = buffer.readByte().toUByte().toInt()
        val keepAlive = decodeMsbLsb(buffer)
        val hasUserName = b1 and 0x80 == 0x80
        val hasPassword = b1 and 0x40 == 0x40
        val willRetain = b1 and 0x20 == 0x20
        val willQos = b1 and 0x18 shr 3
        val willFlag = b1 and 0x04 == 0x04
        val cleanSession = b1 and 0x02 == 0x02
        if (version == MqttVersion.MQTT_3_1_1 || version == MqttVersion.MQTT_5) {
            val zeroReservedFlag = b1 and 0x01 == 0x0
            if (!zeroReservedFlag) {
                // MQTT v3.1.1: The Server MUST validate that the reserved flag in the CONNECT Control Packet is
                // set to zero and disconnect the Client if it is not zero.
                // See https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc385349230
                throw DecoderException("non-zero reserved flag")
            }
        }
        val properties: MqttProperties = decodeProperties(buffer, version)
        return MqttConnectVariableHeader(
            version.protocolName(),
            version.protocolLevel().toInt(),
            hasUserName,
            hasPassword,
            willRetain,
            willQos,
            willFlag,
            cleanSession,
            keepAlive,
            properties
        )
    }

    fun decodeDisconnectVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer,
        ):MqttDisconnectVariableHeader{
        val byteDisconnectReasonCode = buffer.readByte().toUByte()
        val properties: MqttProperties =        decodeProperties(buffer, mqttVersion)
        return MqttDisconnectVariableHeader(ReasonCode.valueOf<Disconnect>(byteDisconnectReasonCode), properties)
    }

    fun decodeConnAckVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer,
    ): MqttConnAckVariableHeader {
        val sessionPresent = buffer.readByte().toUByte().toInt() and 0x01 == 0x01
        val returnCode = buffer.readUByte()
        val properties: MqttProperties = decodeProperties(buffer, mqttVersion)
        return MqttConnAckVariableHeader(ReasonCode.valueOf<MqttConnectReturnCode>(returnCode), sessionPresent, properties)
    }

    fun decodePubCompleteVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer,
    ): MqttPubCompleteVariableHeader {
        val completeReasonCode = buffer.readUByte()
        val properties: MqttProperties = decodeProperties(buffer, mqttVersion)
        return MqttPubCompleteVariableHeader(ReasonCode.valueOf<PubComp>(completeReasonCode), properties)
    }
    fun decodeVariableHeader(
        mqttVersion: MqttVersion,
        buffer: ChunkBuffer
    ): MqttMessageVariableHeader {
        val packetId = decodeMessageId(buffer)
        val properties = decodeProperties(buffer, mqttVersion)
        val mqttVariableHeader = if (mqttVersion == MqttVersion.MQTT_5) {
            MqttMessageVariableHeader(packetId, properties)
        } else {
            MqttMessageVariableHeader(packetId, properties)
        }
        return mqttVariableHeader
    }

    /**
     * @return messageId with numberOfBytesConsumed is 2
     */
    private fun decodeMessageId(buffer: ChunkBuffer): Int {
        val messageId = decodeMsbLsb(buffer)
//        if (!isValidMessageId(messageId)) {
//            throw DecoderException("invalid messageId: $messageId")
//        }
        return messageId
    }

    /**
     * Decodes the payload.
     *
     * @param buffer the buffer to decode from
     * @param messageType  type of the message being decoded
     * @param variableHeader variable header of the same message
     * @return the payload
     */
    private fun decodePayload(
        buffer: ChunkBuffer,
        messageType: MqttMessageType,
        variableHeader: Any?
    ): Any? {
        return when (messageType) {
            CONNECT -> decodeConnectionPayload(
                buffer,
                variableHeader as MqttConnectVariableHeader
            )
            SUBSCRIBE -> decodeSubscribePayload(buffer)
            SUBACK -> decodeSubAckPayload(buffer)
            UNSUBSCRIBE -> decodeUnsubscribePayload(buffer)
            UNSUBACK -> decodeUnsubAckPayload(buffer)
            PUBLISH -> decodePublishPayload(buffer)
            else -> null                 // unknown payload , no byte consumed
        }
    }
    fun decodeConnectionPayload(
        buffer: ChunkBuffer,
        mqttConnectVariableHeader: MqttConnectVariableHeader?
    ): MqttConnectPayload {
        val decodedClientId = decodeString(buffer)
        val mqttVersion = fromProtocolNameAndLevel(
            mqttConnectVariableHeader!!.name, mqttConnectVariableHeader.version.toByte()
        )
        if (!isValidClientId(mqttVersion, maxClientIdLength, decodedClientId)) {
            throw MqttIdentifierRejectedException("invalid clientIdentifier: $decodedClientId")
        }
        var decodedWillTopic: String? = null
        var decodedWillMessage: ByteArray? = null
        val willProperties: MqttProperties
        if (mqttConnectVariableHeader.isWillFlag) {
            willProperties = decodeProperties(buffer, mqttVersion)
            decodedWillTopic = decodeString(buffer, 0, 32767)
            decodedWillMessage = decodeByteArray(buffer)
        } else {
            willProperties = MqttProperties.NO_PROPERTIES
        }
        val decodedUserName = if (mqttConnectVariableHeader.hasUserName) {
            decodeString(buffer)
        } else null
        val decodedPassword = if (mqttConnectVariableHeader.hasPassword) {
            decodeByteArray(buffer)
        } else null
        return MqttConnectPayload(
            decodedClientId,
            willProperties,
            decodedWillTopic,
            decodedWillMessage,
            decodedUserName,
            decodedPassword
        )
    }

    fun decodeSubscribePayload(
        buffer: ChunkBuffer,
    ): MqttSubscribePayload {
        val subscribeTopics: MutableList<MqttTopicSubscription> = ArrayList()
        while (mqttFixedHeader!!.remainingLength >  buffer.readPosition) {
            val decodedTopicName = decodeString(buffer)
            //See 3.8.3.1 Subscription Options of MQTT 5.0 specification for optionByte details
            val optionByte = buffer.readByte().toUByte().toInt()
            val qos = MqttQoS.valueOf(optionByte and 0x03)
            val noLocal = optionByte and 0x04 shr 2 == 1
            val retainAsPublished = optionByte and 0x08 shr 3 == 1
            val retainHandling = MqttSubscriptionOption.RetainedHandlingPolicy.valueOf(
                optionByte and 0x30 shr 4
            )
            val subscriptionOption = MqttSubscriptionOption(
                qos,
                noLocal,
                retainAsPublished,
                retainHandling
            )
            subscribeTopics.add(
                MqttTopicSubscription(
                    decodedTopicName!!, subscriptionOption
                )
            )
        }
        return MqttSubscribePayload(subscribeTopics)//, numberOfBytesConsumed)
    }

    fun decodeSubAckPayload(
        buffer: ChunkBuffer,
    ): MqttSubAckPayload {
        val grantedQos: MutableList<Int> = mutableListOf()
        while ( mqttFixedHeader!!.remainingLength > buffer.readPosition ){
            val reasonCode = buffer.readByte().toUByte().toInt()
            grantedQos.add(reasonCode)
        }
        return MqttSubAckPayload(grantedQos)
    }

    fun decodeUnsubAckPayload(
        buffer: ChunkBuffer,
    ): MqttUnsubAckPayload {
        val reasonCodes: MutableList<Short> = mutableListOf()
        while ( mqttFixedHeader!!.remainingLength > buffer.readPosition ){
            val reasonCode = buffer.readByte().toUByte().toShort()
            reasonCodes.add(reasonCode)
        }
        return MqttUnsubAckPayload(reasonCodes)
    }

    fun decodeUnsubscribePayload(
        buffer: ChunkBuffer,
    ): MqttUnsubscribePayload {
        val unsubscribeTopics: MutableList<String> = ArrayList()
        while (mqttFixedHeader!!.remainingLength > buffer.readPosition  ){
            val decodedTopicName = decodeString(buffer)
            decodedTopicName?.let { unsubscribeTopics.add(it) }
        }
        return MqttUnsubscribePayload(unsubscribeTopics.toList())

    }

    fun decodePublishPayload(
        buffer: ChunkBuffer,
    ): ByteArray {
        val ba = mqttFixedHeader!!.remainingLength - buffer.readPosition
        return buffer.readBytes(ba)
    }

    private fun decodeString(
        buffer: ChunkBuffer,
        minBytes: Int = 0,
        maxBytes: Int = Int.MAX_VALUE
    ): String? {
        val size = decodeMsbLsb(buffer)
        if (size < minBytes || size > maxBytes) {
            return null
        }
        return buffer.readBytes(size).toString(UTF_8)
    }

    /**
     * @return the decoded byte[], numberOfBytesConsumed = byte[].length + 2
     */
    private fun decodeByteArray(buffer: ChunkBuffer): ByteArray {
        val size = decodeMsbLsb(buffer)
        return buffer.readBytes(size)
    }

    /**
     * numberOfBytesConsumed = 2. return decoded result.
     */
    private fun decodeMsbLsb(buffer: ChunkBuffer): Int {
        val min = 0
        val max = 65535
        val msbSize = buffer.readByte().toUByte().toInt()
        val lsbSize = buffer.readByte().toUByte().toInt()
        val result = msbSize shl 8 or lsbSize
        if (result < min || result > max) {
            throw DecoderException("invalid messageId: $result")
        }
        return result
    }

    /**
     * See 1.5.5 Variable Byte Integer section of MQTT 5.0 specification for encoding/decoding rules
     *
     * @param buffer the buffer to decode from
     * @return result pack with a = decoded integer, b = numberOfBytesConsumed. Need to unpack to read them.
     * @throws DecoderException if bad MQTT protocol limits Remaining Length
     */

    private fun  decodeVariableByteInteger(buffer: ChunkBuffer): Int {
        var result = 0
        var isNext: Boolean
        var multiplier = 1
        var loop = 0
        do {
            val byte = buffer.readByte().toUByte().toInt()
            isNext = (byte and 0x80) == 0x80
            result += (byte and 0x7F) * multiplier
            multiplier *= 128
            loop++
        } while (isNext && loop != 4)
        if (loop < 4 && (result < 0 || result > 268435455)) {
            throw DecoderException("variable length exceeds 4 digits ")
        }
        return result
    }

    private suspend fun  decodeVariableByteInteger(
        readChannel: suspend (Int) -> ByteArray,
        messageType: MqttMessageType? = null): Int {
        var result = 0
        var isNext: Boolean
        var multiplier = 1
        var loop = 0
        do {
//            val byte = buffer.readByte().toUByte().toInt()
            val byte = readChannel(1).first().toUByte().toInt()
            isNext = (byte and 0x80) == 0x80
            result += (byte and 0x7F) * multiplier
            multiplier *= 128
            loop++
        } while (isNext && loop != 4)
        if (loop < 4 && (result < 0 || result > 268435455)) {
            throw DecoderException("remaining length exceeds 4 digits ($messageType)")
        }
        return result
    }

    private fun decodeProperties(buffer: ChunkBuffer, mqttVersion: MqttVersion): MqttProperties {
        if (mqttVersion != MqttVersion.MQTT_5) return MqttProperties.NO_PROPERTIES

        val pointStart = buffer.readPosition
        val propertiesLength = decodeVariableByteInteger(buffer)
        val decodedProperties = MqttProperties()
        while ( buffer.readPosition - pointStart < propertiesLength) {
            val propertyId = decodeVariableByteInteger(buffer)
            when (val propertyType = MqttProperties.MqttPropertyType.valueOf(propertyId)) {
                PAYLOAD_FORMAT_INDICATOR, REQUEST_PROBLEM_INFORMATION, REQUEST_RESPONSE_INFORMATION, MAXIMUM_QOS, RETAIN_AVAILABLE, WILDCARD_SUBSCRIPTION_AVAILABLE, SUBSCRIPTION_IDENTIFIER_AVAILABLE, SHARED_SUBSCRIPTION_AVAILABLE -> {
                    val b1 = buffer.readByte().toUByte().toInt()
                    decodedProperties.add(MqttProperties.IntegerProperty(propertyId, b1))
                }
                SERVER_KEEP_ALIVE, RECEIVE_MAXIMUM, TOPIC_ALIAS_MAXIMUM, TOPIC_ALIAS -> {
                    val int2BytesResult = decodeMsbLsb(buffer)
                    decodedProperties.add(
                        MqttProperties.IntegerProperty(
                            propertyId,
                            int2BytesResult
                        )
                    )
                }
                PUBLICATION_EXPIRY_INTERVAL, SESSION_EXPIRY_INTERVAL, WILL_DELAY_INTERVAL, MAXIMUM_PACKET_SIZE -> {
                    val maxPacketSize = buffer.readInt()
                    decodedProperties.add(MqttProperties.IntegerProperty(propertyId, maxPacketSize))
                }
                SUBSCRIPTION_IDENTIFIER -> {
                    val vbIntegerResult = decodeVariableByteInteger(buffer)
                    decodedProperties.add(
                        MqttProperties.IntegerProperty(propertyId, vbIntegerResult)
                    )
                }
                CONTENT_TYPE, RESPONSE_TOPIC, ASSIGNED_CLIENT_IDENTIFIER, AUTHENTICATION_METHOD, RESPONSE_INFORMATION, SERVER_REFERENCE, REASON_STRING -> {
                    val stringResult = decodeString(buffer)
                    decodedProperties.add(MqttProperties.StringProperty(propertyId, stringResult!!))
                }
                USER_PROPERTY -> {
                    val keyResult = decodeString(buffer)
                    val valueResult = decodeString(buffer)
                    decodedProperties.add(MqttProperties.UserProperty(keyResult!!, valueResult!!))
                }
                CORRELATION_DATA, AUTHENTICATION_DATA -> {
                    val binaryDataResult = decodeByteArray(buffer)
                    decodedProperties.add(
                        MqttProperties.BinaryProperty(
                            propertyId,
                            binaryDataResult
                        )
                    )
                }
                else ->                     //shouldn't reach here
                    throw DecoderException("Unknown property type: $propertyType")
            }
        }
        return decodedProperties
    }


    val TOPIC_WILDCARDS = charArrayOf('#', '+')

    private fun isValidPublishTopicName(topicName: String): Boolean {
        // publish topic name must not contain any wildcard
        for (c in TOPIC_WILDCARDS) {
            if (topicName.indexOf(c) >= 0) {
                return false
            }
        }
        return true
    }

    private fun isValidMessageId(messageId: Int): Boolean {
        return messageId != 0
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

    //////////////// MqttFixedHeader
    private fun validateFixedHeader(
        mqttVersion: MqttVersion,
        mqttFixedHeader: MqttFixedHeader
    ): MqttFixedHeader {
        return when (mqttFixedHeader.messageType) {
            PUBREL, SUBSCRIBE, UNSUBSCRIBE -> {
                if (mqttFixedHeader.qosLevel != MqttQoS.AT_LEAST_ONCE)
                    throw DecoderException(mqttFixedHeader.messageType.name + " message must have QoS 1")
                mqttFixedHeader
            }
            AUTH -> {
                if (mqttVersion != MqttVersion.MQTT_5)
                    throw DecoderException("AUTH message requires at least MQTT 5")
                mqttFixedHeader
            }
            else -> mqttFixedHeader
        }
    }

    fun resetUnusedFields(mqttFixedHeader: MqttFixedHeader): MqttFixedHeader {
        return when (mqttFixedHeader.messageType) {
            CONNECT, CONNACK, PUBACK, PUBREC, PUBCOMP, SUBACK, UNSUBACK, PINGREQ, PINGRESP, DISCONNECT -> {
                if (mqttFixedHeader.isDup || mqttFixedHeader.qosLevel != MqttQoS.AT_MOST_ONCE ||
                    mqttFixedHeader.isRetain
                ) {
                    MqttFixedHeader(
                        mqttFixedHeader.messageType, false, MqttQoS.AT_MOST_ONCE,
                        false, mqttFixedHeader.remainingLength
                    )
                } else mqttFixedHeader
            }
            PUBREL, SUBSCRIBE, UNSUBSCRIBE -> {
                if (mqttFixedHeader.isRetain) {
                    MqttFixedHeader(
                        mqttFixedHeader.messageType, mqttFixedHeader.isDup, mqttFixedHeader.qosLevel,
                        false, mqttFixedHeader.remainingLength
                    )
                } else mqttFixedHeader
            }
            else -> mqttFixedHeader
        }
    }
}