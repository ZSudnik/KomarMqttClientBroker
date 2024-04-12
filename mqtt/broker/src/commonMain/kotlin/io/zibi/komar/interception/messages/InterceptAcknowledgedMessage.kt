package io.zibi.komar.interception.messages

import io.zibi.codec.mqtt.MqttQoS
import java.io.Serializable

class InterceptAcknowledgedMessage(
    val msg: StoredMessage,
    val topic: String,
    val username: String,
    val packetID: Int
) : InterceptMessage {
    inner class StoredMessage(val m_payload: ByteArray, var qos: MqttQoS, val m_topic: String) :
        Serializable {
        var isRetained = false
        var clientID: String? = null

        fun getTopic(): String {
            return m_topic
        }

        val payload: ByteArray
            get() = m_payload.clone() //TODO clone no working

        override fun toString(): String {
            return ("PublishEvent{clientID='" + clientID + '\'' + ", m_retain="
                    + isRetained + ", m_qos=" + qos + ", m_topic='" + m_topic + '\'' + '}')
        }

    }
}