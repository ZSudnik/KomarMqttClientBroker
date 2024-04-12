package io.zibi.codec.mqtt

/**
 * Payload of the [MqttSubscribeMessage]
 */
class MqttSubscribePayload(val topicSubscriptions: List<MqttTopicSubscription>) {

    fun toDecByteArray(): ByteArray{
        var byteArray = byteArrayOf()
        topicSubscriptions.forEach {
            byteArray += it.toDecByteArray()
        }
        return byteArray
    }


    override fun toString(): String {
        val builder = StringBuilder(this::class.simpleName?:"null object")
            .append('[')
        for (i in topicSubscriptions.indices) {
            builder.append(topicSubscriptions[i]).append(", ")
        }
        if (topicSubscriptions.isNotEmpty()) {
            builder.setLength(builder.length - 2)
        }
        return builder.append(']').toString()
    }
}
