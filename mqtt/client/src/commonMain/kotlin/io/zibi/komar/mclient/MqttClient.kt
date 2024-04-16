package io.zibi.komar.mclient

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.ChunkBuffer
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
import io.zibi.codec.mqtt.exception.DecoderException
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.ConnectProcessor
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.core.PingProcessor
import io.zibi.komar.mclient.core.PublishProcessor
import io.zibi.komar.mclient.core.SubscribeProcessor
import io.zibi.komar.mclient.core.UnsubscribeProcessor
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
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext
import kotlin.text.Charsets.UTF_8

class MqttClient(
    private val options: MqttConnectOptions,
    private val contextClient: CoroutineContext,
) : IMqttClient {
    private val mqttVer = options.mqttVersion
    private val selectorManager = SelectorManager(contextClient)

    var listener: IListener? = null
    private var receiverJob: Job? = null
    private var pingJob: Job? = null
    private var socket: Socket? = null
    private var byteReadChannel: ByteReadChannel? = null
    private var byteWriteChannel: ByteWriteChannel? = null
    private var connectProcessor: ConnectProcessor = ConnectProcessor()
    private var pingProcessor: PingProcessor = PingProcessor()
    private val subscribeProcessorList: CopyOnWriteArrayList<SubscribeProcessor> =
        CopyOnWriteArrayList()
    private val unsubscribeProcessorList: CopyOnWriteArrayList<UnsubscribeProcessor> =
        CopyOnWriteArrayList()
    private val publishProcessorList: CopyOnWriteArrayList<PublishProcessor> =
        CopyOnWriteArrayList()
    var isConnected = false
        private set
    val isSocketActive
        get() = socket?.isActive ?: false
    var isAutoConnect = true


    private suspend fun closeSocket(e: Exception?) {
        withContext(contextClient) {
            socket?.close()
            listener?.onConnectLost(e ?: Exception("Close socket"))
            shutDown()
        }
    }

    private suspend fun delayConnection(timeout: Long?, idText: String) {
        val actionTime = (timeout ?: options.actionTimeout)
        i("Connect: initializer timeout :${actionTime / 1000} s $idText")
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
                listener?.onConnectFailed(ex)
                RESULT_FAIL
            }
        } != RESULT_SUCCESS

    override suspend fun connectAuto(timeout: Long?) {
        val halfTimeout = (timeout ?: options.actionTimeout) / 2
        while (isAutoConnect) {
            if (!isSocketActive) {
//                listener?.onConnectLost(Exception("Main thread: lose socket"))
                do {
                    if (socketLink(timeout)) {
                        delayConnection(timeout, "socket")
                        closeSocket(Exception("Try socket link"))
                    }
                } while (isAutoConnect && !isSocketActive)
            }
            if (!isConnected) {
//                listener?.onConnectLost(Exception("Main thread: lose connection"))
                do {
                    openReceiver()
                    connectSession()
                    delayConnection(timeout, "connection")
                } while (isAutoConnect && isSocketActive && !isConnected)
            }
//            if (byteReadChannel?.isClosedForWrite == true) closeSocket(Exception("Lose socket link"))
            delay(halfTimeout)
        }
    }

    override suspend fun connectOne(timeout: Long?) {
        socketLink(timeout)
        openReceiver()
        connectSession()
    }

    private fun openReceiver() {
        receiverJob = CoroutineScope(contextClient).launch {
            try {
                val mqttHandler = MQTTHandler()
                while (byteReadChannel?.isClosedForRead == false) {
                    byteReadChannel?.availableForRead
                    if (byteReadChannel == null) return@launch
                    mqttHandler.channelRead()
                }
            } catch (e: Exception) {
                when (e) {
                    is DecoderException -> disConnect()
                    is NullPointerException -> {} //error if byteReadChannel == null after shutDown
                    else -> closeSocket(e)
                }
            }
        }
    }

    private suspend fun connectSession() {
        try {
            connectProcessor = ConnectProcessor()
            isConnected = when (connectProcessor.connect(options, CoroutineScope(contextClient)
            ) { byteArray -> writeChannel(byteArray) }) {
                RESULT_SUCCESS -> true
                RESULT_FAIL -> throw TimeoutException("No answer for connect process. Timeout")
            }
        } catch (ex: Exception) {
            listener?.onConnectFailed(ex)
        } finally {
            if (isConnected) {
                startPingTask()
                listener?.onConnected()

            }
        }
    }

    private suspend fun startPingTask() {
        pingJob = CoroutineScope(contextClient).launch {
            try {
                if ( pingProcessor.isCancelled ) pingProcessor = PingProcessor()
                val keepAliveMs = TimeUnit.SECONDS.toMillis(options.keepAliveTime.toLong())
                while (isActive) {
                    delay(keepAliveMs + options.actionTimeout)
                    when(pingProcessor.ping(options, CoroutineScope(contextClient)) { byteArray -> writeChannel(byteArray) })
                    {
                        RESULT_SUCCESS -> Unit
                        RESULT_FAIL -> closeSocket(
                            TimeoutException("Did not receive a response for a long time : "
                                + "${options.keepAliveTime}s [ping]"))
                    }
                }
            }catch (ex: Exception){
              closeSocket(ex)
            }
        }
    }

    override fun subscribe(topics: List<String>) {
        subscribe(0, topics)
    }

    override fun subscribe(qos: Int, topics: List<String>) {
        CoroutineScope(contextClient).launch {
            val sp = SubscribeProcessor()
            subscribeProcessorList.add(sp)
            try {
                when (sp.subscribe(
                    topics,
                    qos,
                    options,
                    CoroutineScope(contextClient)
                ) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> listener?.onSubscribe(topics.toString())
                    RESULT_FAIL -> listener?.onResponseTimeout("Subscribe：$topics")
                }
            } catch (e: Exception) {
                closeSocket(e)
            } finally {
                subscribeProcessorList.remove(sp)
            }
        }
    }

    override fun unsubscribe(topics: List<String>) {
        CoroutineScope(contextClient).launch {
            val usp = UnsubscribeProcessor()
            unsubscribeProcessorList.add(usp)
            try {
                when (usp.unsubscribe(topics, options, CoroutineScope(contextClient)) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> listener?.onUnsubscribe(topics.toString())
                    RESULT_FAIL -> listener?.onResponseTimeout("Unsubscribe：$topics")
                }
            } catch (e: Exception) {
                closeSocket(e)
            } finally {
                unsubscribeProcessorList.remove(usp)
            }
        }
    }

    override fun publish(topic: String, content: String) {
        CoroutineScope(contextClient).launch {
            val pp = PublishProcessor()
            publishProcessorList.add(pp)
            try {
                when (pp.publish(
                    topic,
                    content,
                    options,
                    CoroutineScope(contextClient)
                ) { byteArray ->
                    writeChannel(byteArray)
                }) {
                    RESULT_SUCCESS -> {}//i("Publish ok：$content")
                    RESULT_FAIL -> listener?.onResponseTimeout("Publish：$content")
                }
            } catch (e: Exception) {
                closeSocket(e)
            } finally {
                publishProcessorList.remove(pp)
            }
        }
    }

    override fun shutDown() {
        disConnectParameters()
//        selectorManager.close()
        byteReadChannel = null
        byteWriteChannel = null
        socket = null
    }

    fun disConnect() {
        CoroutineScope(contextClient).launch {
            try {
                writeChannel(
                    MqttDisconnectMessage(mqttVersion = mqttVer).toDecByteArray(mqttVer)
                )
            } catch (e: Exception) {
                closeSocket(e)
            } finally {
                disConnectParameters()
            }
        }
    }

    private fun disConnectParameters() {
        isConnected = false
        pingJob?.cancel()
        connectProcessor.cancel()
        pingProcessor.cancel()
        subscribeProcessorList.forEach { sp -> sp.cancel() }
        unsubscribeProcessorList.forEach { sp -> sp.cancel() }
        publishProcessorList.forEach { sp -> sp.cancel() }
    }

    private val mutex = Mutex()
    suspend fun readChannel(size: Int): ByteArray {
        if (byteReadChannel?.isClosedForWrite == true)
            closeSocket(Exception("Read channel isClosedForWrite"))

        while (byteReadChannel!!.availableForRead < size) delay(50)
        val byteArray = ByteArray(size)
        mutex.withLock {
            byteReadChannel!!.readFully(byteArray, 0, size)
        }
        return byteArray
    }

    suspend fun writeChannel(byteArray: ByteArray) {
        if (byteWriteChannel?.isClosedForWrite == true) {
            closeSocket(Exception("Write channel isClosedForWrite"))
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
            val mqttFixedHeader = dec.decodeFixedHeader(mqttVer) { size -> readChannel(size) }
            while (byteReadChannel!!.availableForRead < mqttFixedHeader.remainingLength) delay(50)
            chunkBuffer.reset()
            mutex.withLock {
                byteReadChannel!!.readFully(chunkBuffer, mqttFixedHeader.remainingLength)
            }
            i("-->ChannelRead :${mqttFixedHeader.messageType.name}")
            when (mqttFixedHeader.messageType) {
                MqttMessageType.CONNACK -> connectProcessor.processAck(
                    MqttConnAckMessage(
                        mqttFixedHeader, dec.decodeConnAckVariableHeader(mqttVer, chunkBuffer)
                    )
                )

                MqttMessageType.SUBACK -> {
                    val mqttSubAckMessage = MqttSubAckMessage(
                        mqttFixedHeader, dec.decodeVariableHeader(mqttVer, chunkBuffer),
                        dec.decodeSubAckPayload(chunkBuffer)
                    )
                    subscribeProcessorList.forEach { subscribeProcessor ->
                        subscribeProcessor.processAck(mqttSubAckMessage)
                    }
                }

                MqttMessageType.UNSUBACK -> {
                    val mqttUnsubAckMessage = MqttUnsubAckMessage(
                        mqttFixedHeader, dec.decodeVariableHeader(mqttVer, chunkBuffer),
                        dec.decodeUnsubAckPayload(chunkBuffer)
                    )
                    unsubscribeProcessorList.forEach { unsubscribeProcessor ->
                        unsubscribeProcessor.processAck(mqttUnsubAckMessage)
                    }
                }

                MqttMessageType.PUBLISH -> {
                    val mqttPublishVariableHeader = dec.decodePublishVariableHeader(
                        mqttVer, chunkBuffer, mqttFixedHeader
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
                            MqttPubAckMessage(mqttPublishVariableHeader.packetId)
                        writeChannel(mqttPubAckMessage.toDecByteArray(mqttVer))
                    }
                }
                // qos = 1 Ta odpowiedź jest wymagana do wydania
                MqttMessageType.PUBACK -> {
                    val pubAckMessage = MqttPubAckMessage(
                        mqttFixedHeader,
                        dec.decodeVariableHeader(mqttVer, chunkBuffer),
                    )
                    publishProcessorList.forEach { publishProcessor ->
                        publishProcessor.processAck(pubAckMessage)
                    }
                }

                MqttMessageType.PUBREC -> {}
                MqttMessageType.PUBREL -> {}
                MqttMessageType.PUBCOMP -> {}
                MqttMessageType.PINGRESP ->
                    pingProcessor.processAck(MqttPingResponseMessage(mqttFixedHeader))

                MqttMessageType.CONNECT -> {}
                MqttMessageType.SUBSCRIBE -> {}
                MqttMessageType.UNSUBSCRIBE -> {}
                MqttMessageType.PINGREQ -> {}
                MqttMessageType.DISCONNECT -> {
                    val reasonDisconnect =
                        dec.decodeDisconnectVariableHeader(
                            mqttVer,
                            chunkBuffer
                        ).disconnectReasonCode
                    listener?.onDisConnected(reasonDisconnect.toDesc())
                    disConnectParameters()
                }

                else -> {}
            }
        }
    }


}

