package io.zibi.codec.mqtt

/**
 * See [
 * MQTTV3.1/fixed-header](https://public.dhe.ibm.com/software/dw/webservices/ws-mqtt/mqtt-v3r1.html#fixed-header)
 */
data class MqttFixedHeader(
    val messageType: MqttMessageType,
    val isDup: Boolean,
    val qosLevel: MqttQoS,
    val isRetain: Boolean,
    val remainingLength: Int
) {

    fun toDecByteArray(): ByteArray{
        var b1 = 0
        if( isDup) b1 = b1 or 0x08
        if( isRetain) b1 = b1 or 0x01
        b1 = b1 or (messageType.value() shl 4)
        b1 = b1 or (qosLevel.value() shl 1)
        return byteArrayOf( b1.toByte() )
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("messageType=").append(messageType)
            .append(", isDup=").append(isDup)
            .append(", qosLevel=").append(qosLevel)
            .append(", isRetain=").append(isRetain)
            .append(", remainingLength=").append(remainingLength)
            .append(']')
            .toString()
    }
}
