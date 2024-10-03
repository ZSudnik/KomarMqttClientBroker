package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.ReasonCode
import io.zibi.codec.mqtt.reasoncode.UnsubAck

/**
 * Payload for MQTT unsuback message as in V5.
 */
class MqttUnsubAckPayload {
    private val unsubscribeReasonCodes: List<UnsubAck?>

    constructor(vararg unsubscribeReasonCodes: Short) {
        val list: MutableList<UnsubAck?> = ArrayList(unsubscribeReasonCodes.size)
        for (v in unsubscribeReasonCodes) {
            list.add(ReasonCode.valueOf<UnsubAck>((v.toUByte())))
        }
        this.unsubscribeReasonCodes = list.toList()
    }

    constructor(unsubscribeReasonCodes: Iterable<Short>) {
        val list: MutableList<UnsubAck?> = ArrayList()
        for (v in unsubscribeReasonCodes) {
            list.add(ReasonCode.valueOf<UnsubAck>(v.toUByte()))
        }
        this.unsubscribeReasonCodes =list.toList()
    }

    fun unsubscribeReasonCodes(): List<Short> {
        return typedReasonCodesToOrdinal()
    }

    private fun typedReasonCodesToOrdinal(): List<Short> {
        val codes: MutableList<Short> = ArrayList(unsubscribeReasonCodes.size)
        for (code in unsubscribeReasonCodes) {
            codes.add((code!!.byteValue.toInt() and 0xFF).toShort())
        }
        return codes
    }

    fun typedReasonCodes(): List<UnsubAck?> {
        return unsubscribeReasonCodes
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun toDecByteArray(): ByteArray{
        val list = mutableListOf<UByte>()
        unsubscribeReasonCodes.forEach {unsubAck ->
            unsubAck?.let {
                list.add( it.byteValue)
            }
        }
        return list.toUByteArray().toByteArray()
    }


    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("unsubscribeReasonCodes=").append(unsubscribeReasonCodes)
            .append(']')
            .toString()
    }

    companion object {
        private val EMPTY = MqttUnsubAckPayload()
        fun withEmptyDefaults(payload: MqttUnsubAckPayload?): MqttUnsubAckPayload {
            return payload ?: EMPTY
        }
    }
}
