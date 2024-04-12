package io.zibi.komar.mclient.core

import io.zibi.komar.mclient.utils.Log.i
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttPingRequestMessage
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.core.IProcessor
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class PingProcessor(
    private val options: MqttConnectOptions,
    private val scope: CoroutineScope
) : IProcessor {

    private var job: Job? = null
    private val receivedAck = atomic(false)
    var isCancelled : Boolean = false
        private set
    var isDone: Boolean = false
        private set

    @Throws(Exception::class)
    fun ping(
        keepAlive: Int,
        listener: IListener?,
        writeChannel: suspend (ByteArray) -> Unit,
        ) {
        val keepAliveMs = TimeUnit.SECONDS.toMillis(keepAlive.toLong())
        job = scope.launch {
            try {
                val msg = MqttPingRequestMessage().toDecByteArray(options.mqttVersion)
                while (!isCancelled) {
                    receivedAck.value = false
                    i("[ping]-->ping：$msg")
                    writeChannel(msg)
                    delay(keepAliveMs)
                    if (!receivedAck.value) {
                        isCancelled = true
                        val te =
                            TimeoutException("Did not receive a response for a long time : " + keepAlive + "s")
                        i("-->onConnectLost：Did not receive a response PingACK")
                        listener?.onConnectLost(te)
                    }
                }
            } finally {
                isDone = true
            }
        }
    }

    override fun processAck( msg: MqttMessage) {
        receivedAck.value = true
    }

    override fun cancel(){
        isCancelled = true
        receivedAck.value = true
        job?.cancel()
    }

}