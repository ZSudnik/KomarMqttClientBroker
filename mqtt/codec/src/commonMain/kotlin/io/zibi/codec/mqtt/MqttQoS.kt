package io.zibi.codec.mqtt

enum class MqttQoS(private val id: Int) {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2),
    FAILURE(0x80);

    fun value(): Int {
        return id
    }

    fun toByteArray() = byteArrayOf( id.toByte())

    companion object {
//        fun valueOf(value: Int): MqttQoS {
//            return when (value) {
//                0 -> AT_MOST_ONCE
//                1 -> AT_LEAST_ONCE
//                2 -> EXACTLY_ONCE
//                0x80 -> FAILURE
//                else -> throw IllegalArgumentException("invalid QoS: $value")
//            }
//        }
        fun valueOf(value: Int): MqttQoS = entries.find { it.id == value }
            ?: throw IllegalArgumentException("invalid QoS: $value")

    }
}
