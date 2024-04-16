package io.zibi.komar.mclient.core

import io.zibi.komar.mclient.core.ProcessorResult.*
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttPingRequestMessage
import io.zibi.codec.mqtt.util.MqttConnectOptions
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class PingProcessor: IProcessor {

    private var jobLoop: Job? = null
    private val receivedAck = atomic(false)
    private var result: Deferred<ProcessorResult>? = null
    private var jobTime: Job? = null
    var isCancelled : Boolean = false
        private set
    private var isDone: Boolean = false
        private set

//    @Throws(Exception::class)
//    fun pingCycling(
//        options: MqttConnectOptions,
//        scope: CoroutineScope,
//        listener: IListener?,
//        writeChannel: suspend (ByteArray) -> Unit,
//        ) {
//        val keepAliveMs = TimeUnit.SECONDS.toMillis(options.keepAliveTime.toLong())
//        jobLoop = scope.launch {
//            try {
//                val msg = MqttPingRequestMessage().toDecByteArray(options.mqttVersion)
//                while (!isCancelled) {
//                    receivedAck.value = false
//                    writeChannel(msg)
//                    delay(keepAliveMs)
//                    if (!receivedAck.value) {
//                        isCancelled = true
//                        val te = TimeoutException("Did not receive a response for a long time : "
//                                    + "${options.keepAliveTime}s [ping]")
//                        listener?.onConnectFailed(te)
//                    }
//                }
//            } finally {
//                isDone = true
//            }
//        }
//    }

    suspend fun ping(
        options: MqttConnectOptions,
        scope: CoroutineScope,
        writeChannel: suspend (ByteArray) -> Unit,
    ) : ProcessorResult{
        val keepAliveMs = TimeUnit.SECONDS.toMillis(options.keepAliveTime.toLong())
        receivedAck.value = false
        result = scope.async {
            try {
                writeChannel(MqttPingRequestMessage().toDecByteArray(options.mqttVersion))
                jobTime = launch {
                    delay(keepAliveMs)
                }
                jobTime?.join()
                return@async if (receivedAck.value) RESULT_SUCCESS else RESULT_FAIL
            }catch (ex: Exception){
                return@async RESULT_FAIL
            }
        }
        return result?.await() ?: RESULT_FAIL
    }

    override fun processAck( msg: MqttMessage) {
        receivedAck.value = true
        jobTime?.cancel()
    }

    override fun cancel(){
        isCancelled = true
        receivedAck.value = true
        jobLoop?.cancel()
        jobTime?.cancel()
    }

}