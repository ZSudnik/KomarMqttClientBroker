package io.zibi.codec.mqtt

import java.util.Locale

/**
 * MQTT Message Types.
 */
enum class MqttMessageType(private val id: Int)  {
    CONNECT(1),
    CONNACK(2),
    PUBLISH(3),
    PUBACK(4),
    PUBREC(5),
    PUBREL(6),
    PUBCOMP(7),
    SUBSCRIBE(8),
    SUBACK(9),
    UNSUBSCRIBE(10),
    UNSUBACK(11),
    PINGREQ(12),
    PINGRESP(13),
    DISCONNECT(14),
    AUTH(15);

    fun value(): Int {
        return id
    }

    fun toDecByteArray() = byteArrayOf( id.toByte())

    fun toDesc() = this.name.replace("_", " ")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { it2 ->
                if (it2.isLowerCase()) it2.titlecase(Locale.getDefault()) else it2.toString()
            }

    companion object {
        fun valueOf(type: Int): MqttMessageType? = entries.find { it.id == type }
    }
}
