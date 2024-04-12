package io.zibi.komar.persistence

import io.zibi.komar.broker.SessionRegistry.PubRelMarker
import io.zibi.komar.broker.SessionRegistry.PublishedMessage
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttQoS
import org.h2.mvstore.WriteBuffer
import org.h2.mvstore.type.DataType
import org.h2.mvstore.type.StringDataType
import java.nio.ByteBuffer

class EnqueuedMessageValueType : DataType {
    private enum class MessageType {
        PUB_REL_MARKER,
        PUBLISHED_MESSAGE
    }

    private val topicDataType = StringDataType()
    private val payloadDataType = ByteBufDataType()
    override fun compare(a: Any, b: Any): Int {
        return 0
    }

    override fun getMemory(obj: Any): Int {
        if (obj is PubRelMarker) {
            return 1
        }
        val casted = obj as PublishedMessage
        return 1 +  // message type
                1 +  // qos
                topicDataType.getMemory(casted.topic.toString()) +
                payloadDataType.getMemory(casted.payload)
    }

    override fun write(buff: WriteBuffer, obj: Any) {
        if (obj is PublishedMessage) {
            buff.put(MessageType.PUBLISHED_MESSAGE.ordinal.toByte())
            val casted = obj
            buff.put(casted.publishingQos.value().toByte())
            val token = casted.topic.toString()
            topicDataType.write(buff, token)
            payloadDataType.write(buff, casted.payload)
        } else if (obj is PubRelMarker) {
            buff.put(MessageType.PUB_REL_MARKER.ordinal.toByte())
        } else {
            throw IllegalArgumentException("Unrecognized message class " + obj.javaClass)
        }
    }

    override fun write(buff: WriteBuffer, obj: Array<Any>, len: Int, key: Boolean) {
        for (i in 0 until len) {
            write(buff, obj[i])
        }
    }

    override fun read(buff: ByteBuffer): Any {
        val messageType = buff.get()
        return if (messageType.toInt() == MessageType.PUB_REL_MARKER.ordinal) {
            PubRelMarker()
        } else if (messageType.toInt() == MessageType.PUBLISHED_MESSAGE.ordinal) {
            val qos = MqttQoS.valueOf(buff.get().toInt())
            val topicStr = topicDataType.read(buff)
            val payload = payloadDataType.read(buff)
            PublishedMessage(Topic.asTopic(topicStr), qos, payload)
        } else {
            throw IllegalArgumentException("Can't recognize record of type: $messageType")
        }
    }

    override fun read(buff: ByteBuffer, obj: Array<Any>, len: Int, key: Boolean) {
        for (i in 0 until len) {
            obj[i] = read(buff)
        }
    }
}
