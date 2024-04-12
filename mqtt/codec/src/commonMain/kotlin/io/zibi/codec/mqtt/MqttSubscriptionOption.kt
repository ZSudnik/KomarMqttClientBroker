package io.zibi.codec.mqtt

/**
 * Model the SubscriptionOption used in Subscribe MQTT v5 packet
 */
class MqttSubscriptionOption(
    val qos: MqttQoS,
    val isNoLocal: Boolean,
    val isRetainAsPublished: Boolean,
    val retainHandling: RetainedHandlingPolicy
) {
    enum class RetainedHandlingPolicy(private val id: Int) {
        SEND_AT_SUBSCRIBE(0),
        SEND_AT_SUBSCRIBE_IF_NOT_YET_EXISTS(1),
        DONT_SEND_AT_SUBSCRIBE(2);

        fun value(): Int {
            return id
        }

        fun toByteArray() = byteArrayOf(id.toByte())

        companion object {
            fun valueOf(value: Int): RetainedHandlingPolicy {
                return when (value) {
                    0 -> SEND_AT_SUBSCRIBE
                    1 -> SEND_AT_SUBSCRIBE_IF_NOT_YET_EXISTS
                    2 -> DONT_SEND_AT_SUBSCRIBE
                    else -> throw IllegalArgumentException("invalid RetainedHandlingPolicy: $value")
                }
            }
        }
    }

    fun toDecByteArray() : ByteArray {
        var b1 = 0
        b1 = b1 or qos.value()
        if(isNoLocal) b1 = b1 or 0x04
        if(isRetainAsPublished) b1 = b1 or 0x08
        b1 = b1 or ( retainHandling.value() shl 4)
        return byteArrayOf(b1.toByte())
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as MqttSubscriptionOption
        if (isNoLocal != that.isNoLocal) {
            return false
        }
        if (isRetainAsPublished != that.isRetainAsPublished) {
            return false
        }
        return if (qos != that.qos) {
            false
        } else retainHandling == that.retainHandling
    }

    override fun hashCode(): Int {
        var result = qos.hashCode()
        result = 31 * result + if (isNoLocal) 1 else 0
        result = 31 * result + if (isRetainAsPublished) 1 else 0
        result = 31 * result + retainHandling.hashCode()
        return result
    }

    override fun toString(): String {
        return "SubscriptionOption[" +
                "qos=" + qos +
                ", noLocal=" + isNoLocal +
                ", retainAsPublished=" + isRetainAsPublished +
                ", retainHandling=" + retainHandling +
                ']'
    }

    companion object {
        fun onlyFromQos(qos: MqttQoS): MqttSubscriptionOption {
            return MqttSubscriptionOption(
                qos,
                false,
                false,
                RetainedHandlingPolicy.SEND_AT_SUBSCRIBE
            )
        }
    }
}
