package io.zibi.codec.mqtt

import kotlin.text.Charsets.UTF_8

object MqttMessageBuilders {
    fun connect(): ConnectBuilder {
        return ConnectBuilder()
    }

    fun connAck(): ConnAckBuilder {
        return ConnAckBuilder()
    }

    fun publish(): PublishBuilder {
        return PublishBuilder()
    }

    fun subscribe(): SubscribeBuilder {
        return SubscribeBuilder()
    }

    fun unsubscribe(): UnsubscribeBuilder {
        return UnsubscribeBuilder()
    }

    fun pubAck(): PubAckBuilder {
        return PubAckBuilder()
    }

    fun subAck(): SubAckBuilder {
        return SubAckBuilder()
    }

    fun unsubAck(): UnsubAckBuilder {
        return UnsubAckBuilder()
    }

    fun disconnect(): DisconnectBuilder {
        return DisconnectBuilder()
    }

    fun auth(): AuthBuilder {
        return AuthBuilder()
    }

    class PublishBuilder internal constructor() {
        private var topic: String? = null
        private var retained = false
        private var qos: MqttQoS? = null
        private var payload: ByteArray? = null
        private var messageId = 0
        private var mqttProperties: MqttProperties? = null
        fun topicName(topic: String?): PublishBuilder {
            this.topic = topic
            return this
        }

        fun retained(retained: Boolean): PublishBuilder {
            this.retained = retained
            return this
        }

        fun qos(qos: MqttQoS?): PublishBuilder {
            this.qos = qos
            return this
        }

        fun payload(payload: ByteArray?): PublishBuilder {
            this.payload = payload
            return this
        }

        fun messageId(messageId: Int): PublishBuilder {
            this.messageId = messageId
            return this
        }

        fun properties(properties: MqttProperties?): PublishBuilder {
            mqttProperties = properties
            return this
        }

        fun build(): MqttPublishMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.PUBLISH, false, qos!!, retained, 0)
            val mqttVariableHeader = MqttPublishVariableHeader(
                topic!!, messageId, mqttProperties
            )
            return MqttPublishMessage(
                mqttFixedHeader,
                mqttVariableHeader,
                payload
//                Unpooled.buffer().writeBytes(payload)
            )
        }
    }

    class ConnectBuilder internal constructor() {
        private var version = MqttVersion.MQTT_3_1_1
        private var clientId: String? = null
        private var cleanSession = false
        private var hasUser = false
        private var hasPassword = false
        private var keepAliveSecs = 0
        private var willProperties = MqttProperties.NO_PROPERTIES
        private var willFlag = false
        private var willRetain = false
        private var willQos = MqttQoS.AT_MOST_ONCE
        private var willTopic: String? = null
        private var willMessage: ByteArray? = null
        private var username: String? = null
        private var password: ByteArray? = null
        private var properties = MqttProperties.NO_PROPERTIES
        fun protocolVersion(version: MqttVersion): ConnectBuilder {
            this.version = version
            return this
        }

        fun clientId(clientId: String?): ConnectBuilder {
            this.clientId = clientId
            return this
        }

        fun cleanSession(cleanSession: Boolean): ConnectBuilder {
            this.cleanSession = cleanSession
            return this
        }

        fun keepAlive(keepAliveSecs: Int): ConnectBuilder {
            this.keepAliveSecs = keepAliveSecs
            return this
        }

        fun willFlag(willFlag: Boolean): ConnectBuilder {
            this.willFlag = willFlag
            return this
        }

        fun willQoS(willQos: MqttQoS): ConnectBuilder {
            this.willQos = willQos
            return this
        }

        fun willTopic(willTopic: String?): ConnectBuilder {
            this.willTopic = willTopic
            return this
        }

        @Deprecated("use {@link ConnectBuilder#willMessage(byte[])} instead")
        fun willMessage(willMessage: String?): ConnectBuilder {
            willMessage(willMessage?.toByteArray(UTF_8))
            return this
        }

        fun willMessage(willMessage: ByteArray?): ConnectBuilder {
            this.willMessage = willMessage
            return this
        }

        fun willRetain(willRetain: Boolean): ConnectBuilder {
            this.willRetain = willRetain
            return this
        }

        fun willProperties(willProperties: MqttProperties): ConnectBuilder {
            this.willProperties = willProperties
            return this
        }

        fun hasUser(value: Boolean): ConnectBuilder {
            hasUser = value
            return this
        }

        fun hasPassword(value: Boolean): ConnectBuilder {
            hasPassword = value
            return this
        }

        fun username(username: String?): ConnectBuilder {
            hasUser = username != null
            this.username = username
            return this
        }

        @Deprecated("use {@link ConnectBuilder#password(byte[])} instead")
        fun password(password: String?): ConnectBuilder {
            password(password?.toByteArray(UTF_8))
            return this
        }

        fun password(password: ByteArray?): ConnectBuilder {
            hasPassword = password != null
            this.password = password
            return this
        }

        fun properties(properties: MqttProperties): ConnectBuilder {
            this.properties = properties
            return this
        }

        fun build(): MqttConnectMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttConnectVariableHeader = MqttConnectVariableHeader(
                version.protocolName(),
                version.protocolLevel().toInt(),
                hasUser,
                hasPassword,
                willRetain,
                willQos.value(),
                willFlag,
                cleanSession,
                keepAliveSecs,
                properties
            )
            val mqttConnectPayload = MqttConnectPayload(
                clientId,
                willProperties,
                willTopic,
                willMessage,
                username,
                password
            )
            return MqttConnectMessage(
                mqttFixedHeader,
                mqttConnectVariableHeader,
                mqttConnectPayload
            )
        }
    }

    class SubscribeBuilder internal constructor() {
        private var subscriptions: MutableList<MqttTopicSubscription>? = null
        private var messageId = 0
        private var properties: MqttProperties? = null
        fun addSubscription(qos: MqttQoS, topic: String): SubscribeBuilder {
            ensureSubscriptionsExist()
            subscriptions?.add(MqttTopicSubscription(topic, qos))
            return this
        }

        fun addSubscription(topic: String?, option: MqttSubscriptionOption?): SubscribeBuilder {
            ensureSubscriptionsExist()
            subscriptions?.add(MqttTopicSubscription(topic!!, option!!))
            return this
        }

        fun messageId(messageId: Int): SubscribeBuilder {
            this.messageId = messageId
            return this
        }

        fun properties(properties: MqttProperties?): SubscribeBuilder {
            this.properties = properties
            return this
        }

        fun build(): MqttSubscribeMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0)
            val mqttVariableHeader = MqttMessageVariableHeader(messageId, properties)
            val mqttSubscribePayload = MqttSubscribePayload(subscriptions!!)
            return MqttSubscribeMessage(mqttFixedHeader, mqttVariableHeader , mqttSubscribePayload)
        }

        private fun ensureSubscriptionsExist() {
            if (subscriptions == null) {
                subscriptions = mutableListOf()
            }
        }
    }

    class UnsubscribeBuilder internal constructor() {
        private var topicFilters: MutableList<String> = mutableListOf()
        private var messageId = 0
        private var properties: MqttProperties? = null
        fun addTopicFilter(topic: String): UnsubscribeBuilder {
            topicFilters.add(topic)
            return this
        }

        fun messageId(messageId: Int): UnsubscribeBuilder {
            this.messageId = messageId
            return this
        }

        fun properties(properties: MqttProperties?): UnsubscribeBuilder {
            this.properties = properties
            return this
        }

        fun build(): MqttUnsubscribeMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0)
            val mqttVariableHeader = MqttMessageVariableHeader(messageId, properties)
            val mqttSubscribePayload = MqttUnsubscribePayload(topicFilters)
            return MqttUnsubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload)
        }
    }

    interface PropertiesInitializer<T> {
        fun apply(builder: T)
    }

    class ConnAckBuilder constructor() {
        private var returnCode: MqttConnectReturnCode? = null
        private var sessionPresent = false
        private var properties = MqttProperties.NO_PROPERTIES
        private var propsBuilder: ConnAckPropertiesBuilder? = null
        fun returnCode(returnCode: MqttConnectReturnCode?): ConnAckBuilder {
            this.returnCode = returnCode
            return this
        }

        fun sessionPresent(sessionPresent: Boolean): ConnAckBuilder {
            this.sessionPresent = sessionPresent
            return this
        }

        fun properties(properties: MqttProperties): ConnAckBuilder {
            this.properties = properties
            return this
        }

        fun properties(consumer: PropertiesInitializer<ConnAckPropertiesBuilder?>): ConnAckBuilder {
            if (propsBuilder == null) {
                propsBuilder = ConnAckPropertiesBuilder()
            }
            consumer.apply(propsBuilder)
            return this
        }

        fun build(): MqttConnAckMessage {
            if (propsBuilder != null) {
                properties = propsBuilder!!.build()
            }
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttConnAckVariableHeader = MqttConnAckVariableHeader(
                returnCode!!, sessionPresent, properties
            )
            return MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader)
        }
    }

    class ConnAckPropertiesBuilder {
        private var clientId: String? = null
        private var sessionExpiryInterval: Long? = null
        private var receiveMaximum = 0
        private var maximumQos: Byte? = null
        private var retain = false
        private var maximumPacketSize: Long? = null
        private var topicAliasMaximum = 0
        private var reasonString: String? = null
        private val userProperties = MqttProperties.UserProperties()
        private var wildcardSubscriptionAvailable: Boolean? = null
        private var subscriptionIdentifiersAvailable: Boolean? = null
        private var sharedSubscriptionAvailable: Boolean? = null
        private var serverKeepAlive: Int? = null
        private var responseInformation: String? = null
        private var serverReference: String? = null
        private var authenticationMethod: String? = null
        private var authenticationData: ByteArray? = null
        fun build(): MqttProperties {
            val props = MqttProperties()
            if (clientId != null) {
                props.add(
                    MqttProperties.StringProperty(
                        MqttProperties.MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER.value(),
                        clientId!!
                    )
                )
            }
            if (sessionExpiryInterval != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL.value(),
                        sessionExpiryInterval!!.toInt()
                    )
                )
            }
            if (receiveMaximum > 0) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.RECEIVE_MAXIMUM.value(),
                        receiveMaximum
                    )
                )
            }
            if (maximumQos != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.MAXIMUM_QOS.value(),
                        receiveMaximum
                    )
                )
            }
            props.add(
                MqttProperties.IntegerProperty(
                    MqttProperties.MqttPropertyType.RETAIN_AVAILABLE.value(),
                    if (retain) 1 else 0
                )
            )
            if (maximumPacketSize != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.MAXIMUM_PACKET_SIZE.value(),
                        maximumPacketSize!!.toInt()
                    )
                )
            }
            props.add(
                MqttProperties.IntegerProperty(
                    MqttProperties.MqttPropertyType.TOPIC_ALIAS_MAXIMUM.value(),
                    topicAliasMaximum
                )
            )
            if (reasonString != null) {
                props.add(
                    MqttProperties.StringProperty(
                        MqttProperties.MqttPropertyType.REASON_STRING.value(),
                        reasonString!!
                    )
                )
            }
            props.add(userProperties)
            if (wildcardSubscriptionAvailable != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE.value(),
                        if (wildcardSubscriptionAvailable!!) 1 else 0
                    )
                )
            }
            if (subscriptionIdentifiersAvailable != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE.value(),
                        if (subscriptionIdentifiersAvailable!!) 1 else 0
                    )
                )
            }
            if (sharedSubscriptionAvailable != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE.value(),
                        if (sharedSubscriptionAvailable!!) 1 else 0
                    )
                )
            }
            if (serverKeepAlive != null) {
                props.add(
                    MqttProperties.IntegerProperty(
                        MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE.value(),
                        serverKeepAlive!!
                    )
                )
            }
            if (responseInformation != null) {
                props.add(
                    MqttProperties.StringProperty(
                        MqttProperties.MqttPropertyType.RESPONSE_INFORMATION.value(),
                        responseInformation!!
                    )
                )
            }
            if (serverReference != null) {
                props.add(
                    MqttProperties.StringProperty(
                        MqttProperties.MqttPropertyType.SERVER_REFERENCE.value(),
                        serverReference!!
                    )
                )
            }
            if (authenticationMethod != null) {
                props.add(
                    MqttProperties.StringProperty(
                        MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD.value(),
                        authenticationMethod!!
                    )
                )
            }
            if (authenticationData != null) {
                props.add(
                    MqttProperties.BinaryProperty(
                        MqttProperties.MqttPropertyType.AUTHENTICATION_DATA.value(),
                        authenticationData!!
                    )
                )
            }
            return props
        }

        fun sessionExpiryInterval(seconds: Long): ConnAckPropertiesBuilder {
            sessionExpiryInterval = seconds
            return this
        }

        fun receiveMaximum(value: Int): ConnAckPropertiesBuilder {
            if (value <= 0) {
                throw IllegalArgumentException("receiveMaximum : $value (expected: > 0)")
            }
            receiveMaximum = value
            return this
        }

        fun maximumQos(value: Byte): ConnAckPropertiesBuilder {
            require(!(value.toInt() != 0 && value.toInt() != 1)) { "maximum QoS property could be 0 or 1" }
            maximumQos = value
            return this
        }

        fun retainAvailable(retain: Boolean): ConnAckPropertiesBuilder {
            this.retain = retain
            return this
        }

        fun maximumPacketSize(size: Long): ConnAckPropertiesBuilder {
            if (size <= 0) {
                throw IllegalArgumentException("receiveMaximum : $size (expected: > 0)")
            }
            maximumPacketSize = size
            return this
        }

        fun assignedClientId(clientId: String?): ConnAckPropertiesBuilder {
            this.clientId = clientId
            return this
        }

        fun topicAliasMaximum(value: Int): ConnAckPropertiesBuilder {
            topicAliasMaximum = value
            return this
        }

        fun reasonString(reason: String?): ConnAckPropertiesBuilder {
            reasonString = reason
            return this
        }

        fun userProperty(name: String?, value: String?): ConnAckPropertiesBuilder {
            userProperties.add(name!!, value!!)
            return this
        }

        fun wildcardSubscriptionAvailable(value: Boolean): ConnAckPropertiesBuilder {
            wildcardSubscriptionAvailable = value
            return this
        }

        fun subscriptionIdentifiersAvailable(value: Boolean): ConnAckPropertiesBuilder {
            subscriptionIdentifiersAvailable = value
            return this
        }

        fun sharedSubscriptionAvailable(value: Boolean): ConnAckPropertiesBuilder {
            sharedSubscriptionAvailable = value
            return this
        }

        fun serverKeepAlive(seconds: Int): ConnAckPropertiesBuilder {
            serverKeepAlive = seconds
            return this
        }

        fun responseInformation(value: String?): ConnAckPropertiesBuilder {
            responseInformation = value
            return this
        }

        fun serverReference(host: String?): ConnAckPropertiesBuilder {
            serverReference = host
            return this
        }

        fun authenticationMethod(methodName: String?): ConnAckPropertiesBuilder {
            authenticationMethod = methodName
            return this
        }

        fun authenticationData(rawData: ByteArray): ConnAckPropertiesBuilder {
            authenticationData = rawData.clone()
            return this
        }
    }

    class PubAckBuilder internal constructor() {
        private var packetId = 0
        private var reasonCode: Byte = 0
        private var properties: MqttProperties? = null
        fun reasonCode(reasonCode: Byte): PubAckBuilder {
            this.reasonCode = reasonCode
            return this
        }

        fun packetId(packetId: Int): PubAckBuilder {
            this.packetId = packetId
            return this
        }

        @Deprecated("use {@link PubAckBuilder#packetId(int)} instead")
        fun packetId(packetId: Short): PubAckBuilder {
            return packetId(packetId.toInt() and 0xFFFF)
        }

        fun properties(properties: MqttProperties?): PubAckBuilder {
            this.properties = properties
            return this
        }

        fun build(): MqttMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttPubAckVariableHeader =
                MqttPubReplyMessageVariableHeader(packetId, reasonCode, properties)
            return MqttMessage(mqttFixedHeader, mqttPubAckVariableHeader)
        }
    }

    class SubAckBuilder internal constructor() {
        private var packetId = 0
        private var properties: MqttProperties? = null
        private val grantedQoses: MutableList<MqttQoS> = ArrayList()
        fun packetId(packetId: Int): SubAckBuilder {
            this.packetId = packetId
            return this
        }

        @Deprecated("use {@link SubAckBuilder#packetId(int)} instead")
        fun packetId(packetId: Short): SubAckBuilder {
            return packetId(packetId.toInt() and 0xFFFF)
        }

        fun properties(properties: MqttProperties?): SubAckBuilder {
            this.properties = properties
            return this
        }

        fun addGrantedQos(qos: MqttQoS): SubAckBuilder {
            grantedQoses.add(qos)
            return this
        }

        fun addGrantedQoses(vararg qoses: MqttQoS): SubAckBuilder {
//            grantedQoses.addAll(Arrays.asList(*qoses))
            grantedQoses.addAll(qoses.asList())
            return this
        }

        fun build(): MqttSubAckMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttSubAckVariableHeader =
                MqttMessageVariableHeader(packetId, properties)

            //transform to primitive types
            val grantedQoses = IntArray(grantedQoses.size)
            var i = 0
            for (grantedQos in this.grantedQoses) {
                grantedQoses[i++] = grantedQos.value()
            }
            val subAckPayload = MqttSubAckPayload(*grantedQoses)
            return MqttSubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload)
        }
    }

    class UnsubAckBuilder internal constructor() {
        private var packetId = 0
        private var properties: MqttProperties? = null
        private val reasonCodes: MutableList<Short> = ArrayList()
        fun packetId(packetId: Int): UnsubAckBuilder {
            this.packetId = packetId
            return this
        }

        @Deprecated("use {@link UnsubAckBuilder#packetId(int)} instead")
        fun packetId(packetId: Short): UnsubAckBuilder {
            return packetId(packetId.toInt() and 0xFFFF)
        }

        fun properties(properties: MqttProperties?): UnsubAckBuilder {
            this.properties = properties
            return this
        }

        fun addReasonCode(reasonCode: Short): UnsubAckBuilder {
            reasonCodes.add(reasonCode)
            return this
        }

        fun addReasonCodes(vararg reasonCodes: Short): UnsubAckBuilder {
//            this.reasonCodes.addAll(Arrays.asList(*reasonCodes))
            this.reasonCodes.addAll(reasonCodes.asList())
            return this
        }

        fun build(): MqttUnsubAckMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttSubAckVariableHeader =
                MqttMessageVariableHeader(packetId, properties)
            val subAckPayload = MqttUnsubAckPayload(reasonCodes)
            return MqttUnsubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload)
        }
    }

    class DisconnectBuilder internal constructor() {
        private var properties: MqttProperties? = null
        private var reasonCode: Byte = 0
        fun properties(properties: MqttProperties?): DisconnectBuilder {
            this.properties = properties
            return this
        }

        fun reasonCode(reasonCode: Byte): DisconnectBuilder {
            this.reasonCode = reasonCode
            return this
        }

        fun build(): MqttMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttDisconnectVariableHeader =
                MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties)
            return MqttMessage(mqttFixedHeader, mqttDisconnectVariableHeader)
        }
    }

    class AuthBuilder internal constructor() {
        private var properties: MqttProperties? = null
        private var reasonCode: Byte = 0
        fun properties(properties: MqttProperties?): AuthBuilder {
            this.properties = properties
            return this
        }

        fun reasonCode(reasonCode: Byte): AuthBuilder {
            this.reasonCode = reasonCode
            return this
        }

        fun build(): MqttMessage {
            val mqttFixedHeader =
                MqttFixedHeader(MqttMessageType.AUTH, false, MqttQoS.AT_MOST_ONCE, false, 0)
            val mqttAuthVariableHeader =
                MqttReasonCodeAndPropertiesVariableHeader(reasonCode, properties)
            return MqttMessage(mqttFixedHeader, mqttAuthVariableHeader)
        }
    }
}
