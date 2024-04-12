package io.zibi.komar.mclient.core

import io.zibi.komar.mclient.core.MessageIdFactory.release
import io.zibi.komar.mclient.utils.Log.i
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttUnsubAckMessage
import io.zibi.codec.mqtt.MqttUnsubscribeMessage
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.ProcessorResult.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnsubscribeProcessor(
    private val options: MqttConnectOptions,
    private val scope: CoroutineScope
) : IProcessor {

    private var result: Deferred<ProcessorResult>? = null
    private var jobTime: Job? = null
    private var msgId = 0
    private val receivedAck = atomic(false)

    @Throws(Exception::class)
    suspend fun unsubscribe(
        topics: List<String>,
        timeout: Long,
        writeChannel: suspend (ByteArray) -> Unit,
    ): ProcessorResult {
        receivedAck.value = false
        result = scope.async<ProcessorResult> {
            try {
                msgId = MessageIdFactory.get()
                val msg = MqttUnsubscribeMessage(msgId, topics)
                i("-->：$msg")
                writeChannel(msg.toDecByteArray(options.mqttVersion))
                jobTime = launch {
                    delay(timeout)
                }
                jobTime?.join()
                return@async if (receivedAck.value) RESULT_SUCCESS else RESULT_FAIL
            } finally {
                release(msgId)
            }
        }
        return result?.await() ?: RESULT_FAIL
    }

    override fun processAck(msg: MqttMessage) {
        if (msg !is MqttUnsubAckMessage) return
        val variableHeader = msg.variableHeader()
        if (variableHeader.messageId == msgId) {
            receivedAck.value = true
            jobTime?.cancel()
        }
    }

    override fun cancel() {
        result?.cancel()
        jobTime?.cancel()
    }

}