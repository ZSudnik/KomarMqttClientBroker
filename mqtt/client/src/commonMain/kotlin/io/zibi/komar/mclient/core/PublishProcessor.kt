package io.zibi.komar.mclient.core

import io.zibi.komar.mclient.core.MessageIdFactory.release
import io.zibi.komar.mclient.core.ProcessorResult.*
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttPubAckMessage
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.util.MqttConnectOptions
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.Charsets.UTF_8

class PublishProcessor(
    private val options: MqttConnectOptions,
    private val scope: CoroutineScope
) : IProcessor {

    private var result: Deferred<ProcessorResult>? = null
    private var jobTime: Job? = null
    private var msgId = 0
    private val receivedAck = atomic(false)

    @Throws(Exception::class)
    suspend fun publish(
        topic: String,
        content: String,
        timeout: Long,
        writeChannel: suspend (ByteArray) -> Unit,
    ): ProcessorResult {
        receivedAck.value = false
        result = scope.async {
            try {
                msgId = MessageIdFactory.get()
                val msg = MqttPublishMessage.create(
                    topic = topic,
                    payload = content.toByteArray(UTF_8),
                    messageId = msgId
                )
                writeChannel(msg.toDecByteArray(options.mqttVersion))
                jobTime = launch {
                    if (!receivedAck.value) delay(timeout)
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
        if (msg !is MqttPubAckMessage) return
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