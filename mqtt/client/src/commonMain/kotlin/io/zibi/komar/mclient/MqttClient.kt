package io.zibi.komar.mclient

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.ChunkBuffer
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.writeFully
import io.zibi.codec.mqtt.MqttConnAckMessage
import io.zibi.codec.mqtt.MqttDecoder
import io.zibi.codec.mqtt.MqttDisconnectMessage
import io.zibi.codec.mqtt.MqttMessageType
import io.zibi.codec.mqtt.MqttPingResponseMessage
import io.zibi.codec.mqtt.MqttPubAckMessage
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttSubAckMessage
import io.zibi.codec.mqtt.MqttUnsubAckMessage
import io.zibi.codec.mqtt.MqttVersion
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.ConnectProcessor
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.core.PingProcessor
import io.zibi.komar.mclient.core.PublishProcessor
import io.zibi.komar.mclient.core.SubscribeProcessor
import io.zibi.komar.mclient.core.UnsubscribeProcessor
import io.zibi.komar.mclient.utils.Log.e
import io.zibi.komar.mclient.utils.Log.i
import io.zibi.komar.mclient.core.ProcessorResult.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.nio.ByteBuffer
import java.util.concurrent.CancellationException
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.text.Charsets.UTF_8

class MqttClient(
    private val options: MqttConnectOptions,
    private val contextClient: CoroutineContext,
    private val errorConnect: (Boolean) -> Unit,
) : IMqttClient {
    private val mqttVersion = MqttVersion.MQTT_3_1_1
    private val selectorManager = SelectorManager(contextClient)

    var listener: IListener? = null
    private var receiverTask: Job? = null
    private var reconnectJob: Job? = null
    private var socket: Socket? = null
    private var byteReadChannel: ByteReadChannel? = null
    private var byteWriteChannel: ByteWriteChannel? = null
    private var connectProcessor: ConnectProcessor? = null
    private var pingProcessor: PingProcessor? = null
    private val subscribeProcessorList: CopyOnWriteArrayList<SubscribeProcessor> = CopyOnWriteArrayList()
    private val unsubscribeProcessorList: CopyOnWriteArrayList<UnsubscribeProcessor> = CopyOnWriteArrayList()
    private val publishProcessorList: CopyOnWriteArrayList<PublishProcessor> = CopyOnWriteArrayList()
    var isConnected = false
        private set
    val isSocketActive
        get() = socket?.isActive ?: false
    var isAutoConnect = true


    private suspend fun closeSocket(){
        withContext(contextClient) {
            socket?.close()
            listener?.onConnectLost(Exception())
            close()
        }
    }
    private suspend fun delayConnection(timeout: Long?, idText: String){
        val actionTime = (timeout ?: options.actionTimeout)
        i("-->doConnect: initializer timeout :${actionTime/1000} s $idText")
        delay(actionTime)
    }
    private suspend fun socketLink(timeout: Long?): Boolean =
        withTimeoutOrNull(timeout ?: options.actionTimeout) {
            try {
                socket = aSocket(selectorManager).tcp().connect(options.host, options.port)
                byteReadChannel = socket?.openReadChannel()
                byteWriteChannel = socket?.openWriteChannel(autoFlush = true)
                RESULT_SUCCESS
            } catch (ex: Exception) {
                ex.printStackTrace()
                RESULT_FAIL
            }
        } != RESULT_SUCCESS

    @Throws(Exception::class)
    override suspend fun connectAuto(timeout: Long?) {
        val halfTimeout = (timeout ?: options.actionTimeout) / 2
        while (isAutoConnect) {
            delay(halfTimeout)
            if (!isSocketActive) {
                errorConnect(false)
                do {
                    if (socketLink(timeout)) {
                        delayConnection(timeout, "socket")
                        closeSocket()
                    }
                } while (isAutoConnect && !isSocketActive)
            }
            if (!isConnected) {
                errorConnect(false)
                do {
                    openReceiver()
                    connectSession()
                    delayConnection(timeout, "connection")
                } while (isAutoConnect && isSocketActive && !isConnected)
                errorConnect(true)
            }
            if(byteReadChannel?.isClosedForWrite == true) closeSocket()
        }
    }

    @Throws(Exception::class)
    override suspend fun connectOne(timeout: Long?) {
        socketLink(timeout)
        openReceiver()
        connectSession()
    }

    private fun openReceiver() {
        receiverTask = CoroutineScope(contextClient).launch {
            try {
                val mqttHandler = MQTTHandler()
                while (byteReadChannel?.isClosedForRead == false) {
                    byteReadChannel?.availableForRead
                    mqttHandler.channelRead()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                closeSocket()
            }
        }
    }

    @Throws(Exception::class)
    private suspend fun connectSession() {
        try {
            connectProcessor = ConnectProcessor(CoroutineScope(contextClient))
            isConnected = when (connectProcessor?.connect(options) { byteArray -> writeChannel(byteArray) }) {
                RESULT_SUCCESS -> true
                RESULT_FAIL -> throw CancellationException("No answer for connect process. Timeout")
                else -> false
            }
        } catch (ex: Exception) {
            listener?.onConnectFailed(ex)
        } finally {
            if (isConnected)
                onConnected()
        }
    }

    private fun startPingTask() {
        if (pingProcessor == null || pingProcessor!!.isCancelled || pingProcessor!!.isDone)
            pingProcessor = PingProcessor(options, CoroutineScope(contextClient))
        pingProcessor?.ping(
            options.keepAliveTime,
            listener
        ) { byteArray -> writeChannel(byteArray) }
    }

    @Throws(Exception::class)
    override fun subscribe(topics: List<String>) {
        subscribe(0, topics)
    }

    /**
     * @param qos
     * @param topics
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun subscribe(qos: Int, topics: List<String>) {
        CoroutineScope(contextClient).launch {
            val sp = SubscribeProcessor(options, CoroutineScope(contextClient))
            subscribeProcessorList.add(sp)
            try {
                when (sp.subscribe(qos, topics, options.actionTimeout) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> i("-->subscribe ok：$topics")
                    RESULT_FAIL -> throw CancellationException()
                }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> i("-->subscribe CancellationException：$topics")
                    else -> {
                        i("-->subscribe CancellationException：$topics $e")
                        throw e
                    }
                }
            } finally {
                subscribeProcessorList.remove(sp)
            }
        }
    }

    @Throws(Exception::class)
    override fun unsubscribe(topics: List<String>) {
        CoroutineScope(contextClient).launch {
            val usp = UnsubscribeProcessor(options, CoroutineScope(contextClient))
            unsubscribeProcessorList.add(usp)
            try {
                when (usp.unsubscribe(topics, options.actionTimeout) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> i("-->unsubscribe ok：$topics")
                    RESULT_FAIL -> throw CancellationException()
                }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> i("-->unsubscribe CancellationException：$topics")
                    else -> {
                        i("-->unsubscribe CancellationException：$topics $e")
                        throw e
                    }
                }
            } finally {
                unsubscribeProcessorList.remove(usp)
            }
        }
    }

    @Throws(Exception::class)
    override fun publish(topic: String, content: String) {
        CoroutineScope(contextClient).launch {
            val pp = PublishProcessor(options, CoroutineScope(contextClient))
            publishProcessorList.add(pp)
            try {
                when (pp.publish(topic, content, options.actionTimeout) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> i("-->publish ok：$content")
                    RESULT_FAIL -> throw CancellationException()
                }
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> i("-->publish CancellationException：$content")
                    else -> {
//                            throw e
                    }
                }
                e.printStackTrace()
            } finally {
                publishProcessorList.remove(pp)
            }
        }
    }

    override fun close() {
        disConnectParameters()
//        selectorManager.close()
        socket = null
    }

    @Throws(Exception::class)
    fun disConnect() {
        CoroutineScope(contextClient).launch {
            try {
                writeChannel(
                    MqttDisconnectMessage(mqttVersion = mqttVersion).toDecByteArray(
                        options.mqttVersion
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                disConnectParameters()
            }
        }
    }

    private fun disConnectParameters(){
        isConnected = false
        reconnectJob?.cancel()
        connectProcessor?.cancel()
        pingProcessor?.cancel()
        subscribeProcessorList.forEach { sp -> sp.cancel() }
        unsubscribeProcessorList.forEach { sp -> sp.cancel() }
        publishProcessorList.forEach { sp -> sp.cancel() }
    }

    private fun onConnected() {
        startPingTask()
        listener?.onConnected()
    }

    private val mutex = Mutex()
    suspend fun readChannel(size: Int): ByteArray {
        if(byteReadChannel?.isClosedForWrite == true) closeSocket()

        while (byteReadChannel!!.availableForRead < size) delay(50)
        val byteArray = ByteArray(size)
        mutex.withLock {
            byteReadChannel!!.readFully(byteArray, 0, size)
        }
        return byteArray
    }

    suspend fun writeChannel(byteArray: ByteArray) {
        if(byteWriteChannel?.isClosedForWrite == true) {
            closeSocket()
            return
        }
        mutex.withLock {
            byteWriteChannel!!.writeFully(byteArray)
        }
    }

    internal inner class MQTTHandler {
        private val dec = MqttDecoder()
        private val chunkBuffer = ChunkBuffer(ByteBuffer.allocate(dec.maxBytesInMessage))

        @Throws(Exception::class)
        suspend fun channelRead() {
            val mqttFixedHeader = dec.decodeFixedHeader(mqttVersion) { size -> readChannel(size) }
            while (byteReadChannel!!.availableForRead < mqttFixedHeader.remainingLength) delay(50)
            chunkBuffer.reset()
            mutex.withLock {
                byteReadChannel!!.readFully(chunkBuffer, mqttFixedHeader.remainingLength)
            }
            e("-->channelRead :${mqttFixedHeader.messageType.name}")
            try {
                when (mqttFixedHeader.messageType) {
                    MqttMessageType.CONNACK -> connectProcessor?.processAck(
                        MqttConnAckMessage(
                            mqttFixedHeader,
                            dec.decodeConnAckVariableHeader(mqttVersion, chunkBuffer)
                        )
                    )

                    MqttMessageType.SUBACK -> {
                        val mqttSubAckMessage = MqttSubAckMessage(
                            mqttFixedHeader,
                            dec.decodeVariableHeader(mqttVersion, chunkBuffer),
                            dec.decodeSubAckPayload(chunkBuffer)
                        )
                        subscribeProcessorList.forEach { subscribeProcessor ->
                            subscribeProcessor.processAck(mqttSubAckMessage)
                        }
                    }

                    MqttMessageType.UNSUBACK -> {
                        val mqttUnsubAckMessage = MqttUnsubAckMessage(
                            mqttFixedHeader,
                            dec.decodeVariableHeader(mqttVersion, chunkBuffer),
                            dec.decodeUnsubAckPayload(chunkBuffer)
                        )
                        unsubscribeProcessorList.forEach { unsubscribeProcessor ->
                            unsubscribeProcessor.processAck(mqttUnsubAckMessage)
                        }
                    }

                    MqttMessageType.PUBLISH -> {
                        val mqttPublishVariableHeader = dec.decodePublishVariableHeader(
                            mqttVersion,
                            chunkBuffer,
                            mqttFixedHeader
                        )
                        val publishMessage = MqttPublishMessage(
                            mqttFixedHeader,
                            mqttPublishVariableHeader,
                            dec.decodePublishPayload(chunkBuffer)
                        )

                        listener?.onMessageArrived(
                            mqttPublishVariableHeader.topicName,
                            publishMessage.payload().toString(UTF_8)
                        )
                        if (mqttFixedHeader.qosLevel in listOf(
                                MqttQoS.AT_LEAST_ONCE,
                                MqttQoS.EXACTLY_ONCE
                            )
                        ) {
                            // Wiadomości z poziomami qos 1 i 2 wymagają wysłania potwierdzenia
                            // Uwaga: przed wysłaniem potwierdzenia należy je całkowicie przeczytać.
                            val mqttPubAckMessage =
                                MqttPubAckMessage.create(mqttPublishVariableHeader.packetId)
                            i("-->PUBLISH：$mqttPubAckMessage")
                            writeChannel(mqttPubAckMessage.toDecByteArray(options.mqttVersion))
                        } else {
                            i("-->PUBLISH qosLevel=0")
                        }
                    }
                    // qos = 1 Ta odpowiedź jest wymagana do wydania
                    MqttMessageType.PUBACK -> {
                        val pubAckMessage = MqttPubAckMessage(
                            mqttFixedHeader,
                            dec.decodeVariableHeader(mqttVersion, chunkBuffer),
                        )
                        publishProcessorList.forEach { publishProcessor ->
                            publishProcessor.processAck(pubAckMessage)
                        }
                    }

                    MqttMessageType.PUBREC -> {
                        e("-->PUBREC：")
                    }

                    MqttMessageType.PUBREL -> {
                        e("-->PUBREL：")
                    }

                    MqttMessageType.PUBCOMP -> {
                        e("-->PUBREL：")
                    }

                    MqttMessageType.PINGRESP ->
                        pingProcessor?.processAck(MqttPingResponseMessage(mqttFixedHeader))

                    MqttMessageType.CONNECT -> {}
                    MqttMessageType.SUBSCRIBE -> {}
                    MqttMessageType.UNSUBSCRIBE -> {}
                    MqttMessageType.PINGREQ -> {}
                    MqttMessageType.DISCONNECT -> {
                        val reasonDisconnect =
                            dec.decodeDisconnectVariableHeader(mqttVersion, chunkBuffer)
                                .disconnectReasonCode
                        listener?.onConnectLost(Exception(reasonDisconnect.toDesc()))
                        disConnectParameters()
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    companion object {
        private const val MIN_RECONNECT_INTERVAL = 1800L
    }

}


// private fun doReconnect(
//        maxTimes: Long,
//        timeout: Long,
//        t: Throwable
//    ) {
//        reconnectJob = CoroutineScope(contextClient).launch {
//            var interval = MIN_RECONNECT_INTERVAL
//            if (timeout > 0) {
//                interval = timeout / maxTimes
//                if (interval < MIN_RECONNECT_INTERVAL) interval = MIN_RECONNECT_INTERVAL
//            }
//            var bSuccess = false
//            var num = 0
//            val start = System.nanoTime()
//            do {
//                ++num
//                i("-->reconnectJob：$num")
//                listener?.onReconnectStart(num)
//                val begin = System.nanoTime()
//                try {
//                    connect(interval)
//                    i("<--reconnectJob：$num")
//                    bSuccess = true
//                    break
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    i("<--reconnectJob：$num")
//                }
//                if (maxTimes <= num) break
//                if (timeout > 0) {
//                    val spendTotal = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
//                    if (timeout <= spendTotal) {
//                        break
//                    }
//                }
//                val spend = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin)
//                val sleepTime = interval - spend
//                if (sleepTime > 0) delay(sleepTime)
//            } while (isActive)
//            if (isActive) {
//                if (!bSuccess) {
//                    close()
//                    listener?.onConnectLost(t)
//                }
//            }
//        }
//    }