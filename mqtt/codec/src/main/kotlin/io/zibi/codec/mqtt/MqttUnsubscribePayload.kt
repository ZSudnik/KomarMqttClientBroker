package io.zibi.codec.mqtt

import kotlin.text.Charsets.UTF_8

/**
 * Payload of the [MqttUnsubscribeMessage]
 */
class MqttUnsubscribePayload(val topics: List<String>) {

    fun toDecByteArray(): ByteArray{
        var array = byteArrayOf()
        topics.forEach {
            array += it.toByteArray(UTF_8)
        }
        return array
    }

    override fun toString(): String {
        val builder = StringBuilder(this::class.simpleName ?: "null object")
            .append('[')
        for (i in topics.indices) {
            builder.append("topicName = ").append(topics[i]).append(", ")
        }
        if (topics.isNotEmpty()) {
            builder.setLength(builder.length - 2)
        }
        return builder.append("]").toString()
    }
}
