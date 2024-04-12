package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.ReasonCode

/**
 * MQTT Message Types.
 */
enum class MqttMessageType(private val id: Int) : ReasonCode {
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

    override fun toDecByteArray() = byteArrayOf( id.toByte())

    override fun toDesc() = ReasonCode.makeDesc( this.name )


    companion object {
        fun valueOf(type: Int): MqttMessageType? = entries.find { it.id == type }
    }
}
