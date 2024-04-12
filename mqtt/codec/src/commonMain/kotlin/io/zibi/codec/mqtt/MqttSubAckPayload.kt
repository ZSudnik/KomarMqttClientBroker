package io.zibi.codec.mqtt

import io.zibi.codec.mqtt.reasoncode.SubAck
import java.util.Collections

/**
 * Payload of the [MqttSubAckMessage]
 */
class MqttSubAckPayload {
    private val reasonCodes: List<SubAck?>

    constructor(vararg reasonCodes: Int) {
        val list: MutableList<SubAck?> = ArrayList(reasonCodes.size)
        for (v in reasonCodes) {
            list.add(SubAck.valueOf(v.toUByte()))
        }
        this.reasonCodes = Collections.unmodifiableList(list)
    }

    constructor(vararg reasonCodes: SubAck) {
        val list: MutableList<SubAck?> = ArrayList(reasonCodes.size)
        list.addAll(listOf(*reasonCodes))
        this.reasonCodes = Collections.unmodifiableList(list)
    }

    constructor(reasonCodes: Iterable<Int>) {
        val list: MutableList<SubAck?> = mutableListOf()
        for (v in reasonCodes) {
            list.add(SubAck.valueOf(v.toUByte()))
        }
        this.reasonCodes = Collections.unmodifiableList(list)
    }

    fun grantedQoSLevels(): List<Int> {
        val qosLevels: MutableList<Int> = ArrayList(reasonCodes.size)
        for (code in reasonCodes) {
            if (code!!.byteValue.toInt() and 0xFF > MqttQoS.EXACTLY_ONCE.value()) {
                qosLevels.add(MqttQoS.FAILURE.value())
            } else {
                qosLevels.add(code.byteValue.toInt() and 0xFF)
            }
        }
        return qosLevels
    }

    fun reasonCodes(): List<Int> {
        return typedReasonCodesToOrdinal()
    }

    private fun typedReasonCodesToOrdinal(): List<Int> {
        val intCodes: MutableList<Int> = ArrayList(reasonCodes.size)
        for (code in reasonCodes) {
            intCodes.add(code!!.byteValue.toInt() and 0xFF)
        }
        return intCodes
    }

    fun typedReasonCodes(): List<SubAck?> {
        return reasonCodes
    }
    @OptIn(ExperimentalUnsignedTypes::class)
    fun toDecByteArray(): ByteArray{
        val list = mutableListOf<UByte>()
        reasonCodes.forEach {subAck ->
            subAck?.let {
                list.add( it.byteValue)
            }
        }
        return list.toUByteArray().toByteArray()
    }

    override fun toString(): String {
        return StringBuilder(this::class.simpleName?:"null object")
            .append('[')
            .append("reasonCodes=").append(reasonCodes)
            .append(']')
            .toString()
    }
}
