package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.util.toDecByteArray
import io.zibi.codec.mqtt.util.toDecByteArrayFour
import io.zibi.codec.mqtt.util.variableLengthInt

/**
 * MQTT Properties container
 */
class MqttProperties private constructor(private val canModify: Boolean) {

    enum class MqttPropertyType(val id: Int) {
        // single byte properties
        PAYLOAD_FORMAT_INDICATOR(0x01),
        REQUEST_PROBLEM_INFORMATION(0x17),
        REQUEST_RESPONSE_INFORMATION(0x19),
        MAXIMUM_QOS(0x24),
        RETAIN_AVAILABLE(0x25),
        WILDCARD_SUBSCRIPTION_AVAILABLE(0x28),
        SUBSCRIPTION_IDENTIFIER_AVAILABLE(0x29),
        SHARED_SUBSCRIPTION_AVAILABLE(0x2A),

        // two bytes properties
        SERVER_KEEP_ALIVE(0x13),
        RECEIVE_MAXIMUM(0x21),
        TOPIC_ALIAS_MAXIMUM(0x22),
        TOPIC_ALIAS(0x23),

        // four bytes properties
        PUBLICATION_EXPIRY_INTERVAL(0x02),
        SESSION_EXPIRY_INTERVAL(0x11),
        WILL_DELAY_INTERVAL(0x18),
        MAXIMUM_PACKET_SIZE(0x27),

        // Variable Byte Integer
        SUBSCRIPTION_IDENTIFIER(0x0B),

        // UTF-8 Encoded String properties
        CONTENT_TYPE(0x03),
        RESPONSE_TOPIC(0x08),
        ASSIGNED_CLIENT_IDENTIFIER(0x12),
        AUTHENTICATION_METHOD(0x15),
        RESPONSE_INFORMATION(0x1A),
        SERVER_REFERENCE(0x1C),
        REASON_STRING(0x1F),
        USER_PROPERTY(0x26),

        // Binary Data
        CORRELATION_DATA(0x09),
        AUTHENTICATION_DATA(0x16),


        UNKNOWN_PROPERTY(0x7F);

        fun value(): Int {
            return id
        }


        companion object {
            private val mapTyp = entries.associateBy(MqttPropertyType::id)
            fun valueOf(type: Int): MqttPropertyType = mapTyp[type] ?: UNKNOWN_PROPERTY
        }
    }

    fun toDecByteArray(): ByteArray{
        return if(canModify){
            var byteArray = byteArrayOf()
            listAll().forEach { mqttProperty ->
                byteArray += mqttProperty.toDecByteArray()
            }
            variableLengthInt(byteArray.size) + byteArray
        }else byteArrayOf()
    }



    /**
     * MQTT property base class
     *
     * @param <T> property type
    </T> */
    abstract class MqttProperty<T> protected constructor( val propertyId: Int, val value: T) {
        /**
         * Get MQTT property value
         *
         * @return property value
         */
        fun value(): T {
            return value
        }

        /**
         * Get MQTT property ID
         * @return property ID
         */
        fun propertyId(): Int {
            return propertyId
        }

        abstract fun toDecByteArray(): ByteArray

        override fun hashCode(): Int {
            return propertyId + 31 * value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            val that = other as MqttProperty<*>
            return propertyId == that.propertyId && value == that.value
        }
    }

    class IntegerProperty(propertyId: Int, value: Int) : MqttProperty<Int>(propertyId, value) {
        override fun toDecByteArray(): ByteArray {
           val valueByteArray =  when (MqttPropertyType.valueOf(propertyId)) {
               MqttPropertyType.PAYLOAD_FORMAT_INDICATOR, MqttPropertyType.REQUEST_PROBLEM_INFORMATION, MqttPropertyType.REQUEST_RESPONSE_INFORMATION, MqttPropertyType.MAXIMUM_QOS, MqttPropertyType.RETAIN_AVAILABLE, MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE, MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE, MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE ->
                   byteArrayOf(value.toByte())
               MqttPropertyType.SERVER_KEEP_ALIVE, MqttPropertyType.RECEIVE_MAXIMUM, MqttPropertyType.TOPIC_ALIAS_MAXIMUM, MqttPropertyType.TOPIC_ALIAS ->
                   value.toUShort().toDecByteArray()
               MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL, MqttPropertyType.SESSION_EXPIRY_INTERVAL, MqttPropertyType.WILL_DELAY_INTERVAL, MqttPropertyType.MAXIMUM_PACKET_SIZE ->
                   value.toDecByteArrayFour()
               MqttPropertyType.SUBSCRIPTION_IDENTIFIER ->
                   variableLengthInt( value)
               else -> byteArrayOf()
           }
            return variableLengthInt(propertyId) + valueByteArray
        }

        override fun toString(): String {
            return "IntegerProperty($propertyId, $value)"
        }
    }

    class StringProperty(propertyId: Int, value: String) :
        MqttProperty<String>(propertyId, value) {
        override fun toDecByteArray(): ByteArray {
            //MqttPropertyType.CONTENT_TYPE, MqttPropertyType.RESPONSE_TOPIC, MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER, MqttPropertyType.AUTHENTICATION_METHOD, MqttPropertyType.RESPONSE_INFORMATION, MqttPropertyType.SERVER_REFERENCE, MqttPropertyType.REASON_STRING
            return variableLengthInt(propertyId) + value.toDecByteArray()
        }

        override fun toString(): String {
            return "StringProperty($propertyId, $value)"
        }
    }

    class StringPair(val key: String, val value: String) {
        override fun hashCode(): Int {
            return key.hashCode() + 31 * value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            val that = other as StringPair
            return that.key == key && that.value == value
        }
    }

    //User properties are the only properties that may be included multiple times and
    //are the only properties where ordering is required. Therefore, they need a special handling
    class UserProperties() : MqttProperty<MutableList<StringPair>>(
        propertyId =  MqttPropertyType.USER_PROPERTY.id,
        value = mutableListOf<StringPair>()
    ) {
        /**
         * Create user properties from the collection of the String pair values
         *
         * @param values string pairs. Collection entries are copied, collection itself isn't shared
         */
        constructor(values: Collection<StringPair>) : this() {
                this.value.addAll(values)
        }

        fun add(pair: StringPair) {
            value.add(pair)
        }

        fun add(key: String, value: String) {
            this.value.add(StringPair(key, value))
        }

        override fun toDecByteArray(): ByteArray {
            //MqttPropertyType.USER_PROPERTY
            var byteArray = byteArrayOf()
            value.forEach {
                byteArray += variableLengthInt(propertyId) +
                        it.key.toDecByteArray() + it.value.toDecByteArray()
            }
            return byteArray
        }

        override fun toString(): String {
            val builder = StringBuilder("UserProperties(")
            var first = true
            value.let {
                for (pair in it) {
//                    if(pair != null) {
                        if (!first) {
                            builder.append(", ")
                        }
                        builder.append(pair.key + "->" + pair.value)
                        first = false
//                    }
                }
            }
            builder.append(")")
            return builder.toString()
        }

        companion object {
            fun fromUserPropertyCollection(properties: Collection<UserProperty>): UserProperties {
                val userProperties = UserProperties()
                for (property in properties) {
                    userProperties.add(StringPair(property.value.key, property.value.value))
                }
                return userProperties
            }
        }
    }

    class UserProperty(key: String, value: String) :
        MqttProperty<StringPair>(MqttPropertyType.USER_PROPERTY.id, StringPair(key, value)) {
        override fun toDecByteArray(): ByteArray {
            return variableLengthInt(propertyId) +
                    value.key.toDecByteArray() + value.value.toDecByteArray()
        }

        override fun toString(): String {
            return "UserProperty(" + value.key + ", " + value.value + ")"
        }
    }

    class BinaryProperty(propertyId: Int, value: ByteArray) :
        MqttProperty<ByteArray>(propertyId, value) {
        override fun toDecByteArray(): ByteArray {
            //MqttPropertyType.CORRELATION_DATA, MqttPropertyType.AUTHENTICATION_DATA
            return variableLengthInt(propertyId) +
                    value.size.toUShort().toDecByteArray() + value.toDecByteArray()
        }

        override fun toString(): String {
            return "BinaryProperty(" + propertyId + ", " + value.size + " bytes)"
        }
    }

    constructor() : this(true)

    private var props: MutableMap<Int, MqttProperty<*>> = mutableMapOf()
    private var userProperties: MutableList<UserProperty> = mutableListOf()
    private var subscriptionIds: MutableList<IntegerProperty> = mutableListOf()

    fun add(type: MqttPropertyType, value: Any){
        if (!canModify) {
            throw UnsupportedOperationException("adding property isn't allowed")
        }
        when(type){
            MqttPropertyType.CORRELATION_DATA, MqttPropertyType.AUTHENTICATION_DATA ->
                if(value is ByteArray){
                    props[type.id] = BinaryProperty(type.id, value)
                }else throw IllegalArgumentException("Value must be an ByteArray")
            MqttPropertyType.PAYLOAD_FORMAT_INDICATOR, MqttPropertyType.REQUEST_PROBLEM_INFORMATION, MqttPropertyType.REQUEST_RESPONSE_INFORMATION, MqttPropertyType.MAXIMUM_QOS, MqttPropertyType.RETAIN_AVAILABLE, MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE, MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE, MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE, MqttPropertyType.SERVER_KEEP_ALIVE, MqttPropertyType.RECEIVE_MAXIMUM, MqttPropertyType.TOPIC_ALIAS_MAXIMUM, MqttPropertyType.TOPIC_ALIAS, MqttPropertyType.PUBLICATION_EXPIRY_INTERVAL, MqttPropertyType.SESSION_EXPIRY_INTERVAL, MqttPropertyType.WILL_DELAY_INTERVAL, MqttPropertyType.MAXIMUM_PACKET_SIZE ->
                if(value is Int){
                    props[type.id] = IntegerProperty(type.id, value)
                }else throw IllegalArgumentException("Value must be an Integer")
            MqttPropertyType.SUBSCRIPTION_IDENTIFIER ->
                if (value is Int) {
                    subscriptionIds.add(IntegerProperty(type.id, value))
                } else throw IllegalArgumentException("Subscription ID must be an integer property")
            MqttPropertyType.CONTENT_TYPE, MqttPropertyType.RESPONSE_TOPIC, MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER, MqttPropertyType.AUTHENTICATION_METHOD, MqttPropertyType.RESPONSE_INFORMATION, MqttPropertyType.SERVER_REFERENCE, MqttPropertyType.REASON_STRING ->{
                if(value is String){
                    props[type.id] = StringProperty(type.id, value)
                }else throw IllegalArgumentException("Value must be an String")
            }
            MqttPropertyType.USER_PROPERTY ->{
                when(value){
                    is StringPair -> userProperties.add(UserProperty(value.key, value.value) )
                    is UserProperty -> userProperties.add(value)
                    else -> throw IllegalArgumentException("User property must be of UserProperty or UserProperties type")
                }
            }
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    fun add(property: MqttProperty<*>) {
        if (!canModify) {
            throw UnsupportedOperationException("adding property isn't allowed")
        }
        when(property.propertyId){
            MqttPropertyType.USER_PROPERTY.id->{
                when(property){
                    is UserProperty -> userProperties.add(property)
                    is UserProperties -> {
                        property.value.let {
                            for (pair in it) {
//                                if(pair!= null)
                                userProperties.add(UserProperty(pair.key, pair.value))
                            }
                        }
                    }
                    else -> throw IllegalArgumentException("User property must be of UserProperty or UserProperties type")
                }
            }
            MqttPropertyType.SUBSCRIPTION_IDENTIFIER.id ->
                if (property is IntegerProperty) {
                    subscriptionIds.add(property)
                } else {
                    throw IllegalArgumentException("Subscription ID must be an integer property")
                }
            else -> props[property.propertyId] = property
        }
    }

    fun listAll(): List<MqttProperty<*>> {
        val propValues: MutableList<MqttProperty<*>> = mutableListOf()
            propValues.addAll(props.values)
            propValues.addAll(subscriptionIds)
            propValues.add(UserProperties.fromUserPropertyCollection(userProperties))
        return propValues
    }

    val isEmpty: Boolean
        get() = props.isEmpty()

    /**
     * Get property by ID. If there are multiple properties of this type (can be with Subscription ID)
     * then return the first one.
     *
     * @param propertyId ID of the property
     * @return a property if it is set, null otherwise
     */
    fun getProperty(propertyId: Int): MqttProperty<*>? {
        return when(propertyId){
            MqttPropertyType.USER_PROPERTY.id -> UserProperties.fromUserPropertyCollection(
                userProperties
            )
            MqttPropertyType.SUBSCRIPTION_IDENTIFIER.id -> {
                if (subscriptionIds.isEmpty()) null else subscriptionIds[0]
            }
            else -> props[propertyId]
        }
    }

    /**
     * Get properties by ID.
     * Some properties (Subscription ID and User Properties) may occur multiple times,
     * this method returns all their values in order.
     *
     * @param propertyId ID of the property
     * @return all properties having specified ID
     */
    fun getProperties(propertyId: Int): List<MqttProperty<*>> {
        return when(propertyId){
            MqttPropertyType.USER_PROPERTY.id ->userProperties
            MqttPropertyType.SUBSCRIPTION_IDENTIFIER.id -> {
                val subscrIds = mutableListOf<IntegerProperty>()
                subscriptionIds.forEach { intProperty ->
                    subscrIds.add( intProperty)
                }
                subscrIds
            }
            else -> if( props.containsKey(propertyId)) listOf(props[propertyId]!!) else emptyList()
        }
    }

    companion object {
        val NO_PROPERTIES = MqttProperties(false)
        fun withEmptyDefaults(properties: MqttProperties?): MqttProperties {
            return properties ?: NO_PROPERTIES
        }
    }
}
