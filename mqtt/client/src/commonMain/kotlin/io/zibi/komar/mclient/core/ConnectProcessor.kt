package io.zibi.komar.mclient.core

import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.utils.Log.i
import io.zibi.komar.mclient.core.ProcessorResult.*
import io.zibi.codec.mqtt.MqttConnAckMessage
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttConnectReturnCode
import io.zibi.codec.mqtt.MqttMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.atomicfu.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ConnectProcessor(
    private val scope: CoroutineScope
) : IProcessor {

    private val atomicCD = atomic<CompletableDeferred<ProcessorResult>>(CompletableDeferred<ProcessorResult>())
    private val completableDeferred = atomicCD.value
    private var job: Job? = null

//    @Throws(Exception::class)
    suspend fun connect(
        options: MqttConnectOptions,
        writeChannel: suspend (ByteArray) -> Unit,
    ): ProcessorResult {
        val msg = MqttConnectMessage(options)
        i("-->connect:：$msg")
        writeChannel(msg.toDecByteArray(options.mqttVersion))
        job = scope.launch {
            delay(options.actionTimeout)
            try {
                atomicCD.loop {
                    if (!it.isCompleted)
                        it.complete(RESULT_FAIL)
                    return@launch
                }
            } finally {
                job?.cancel()
            }
        }
        return completableDeferred.await()
    }


    override fun processAck(msg: MqttMessage) {
        if (msg !is MqttConnAckMessage) return
        val mqttConnAckVariableHeader = msg.variableHeader()
        val err = when (val res = mqttConnAckVariableHeader.connectReturnCode()) {
            MqttConnectReturnCode.CONNECTION_ACCEPTED -> {
                atomicCD.loop {
                    job?.cancel()
                    it.complete(RESULT_SUCCESS)
                    return
                }
                return
            }
            else -> res.toDesc()
        }
        atomicCD.loop {
            if (!it.isCompleted) {
                job?.cancel()
                it.completeExceptionally(CancellationException(err))
            }
            return
        }
    }

    override fun cancel() {
        completableDeferred.cancel()
        job?.cancel()
    }
}