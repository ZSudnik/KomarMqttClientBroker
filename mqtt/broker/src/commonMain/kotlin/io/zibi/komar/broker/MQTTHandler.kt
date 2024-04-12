package io.zibi.komar.broker

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.ChunkBuffer
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.writeFully
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttDecoder
import io.zibi.codec.mqtt.MqttDisconnectMessage
import io.zibi.codec.mqtt.MqttFixedHeader
import io.zibi.codec.mqtt.MqttMessage
import io.zibi.codec.mqtt.MqttMessageType
import io.zibi.codec.mqtt.MqttPingRequestMessage
import io.zibi.codec.mqtt.MqttPingResponseMessage
import io.zibi.codec.mqtt.MqttPubAckMessage
import io.zibi.codec.mqtt.MqttPubCompleteMessage
import io.zibi.codec.mqtt.MqttPubReceivedMessage
import io.zibi.codec.mqtt.MqttPubReleaseMessage
import io.zibi.codec.mqtt.MqttPublishMessage
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttSubAckMessage
import io.zibi.codec.mqtt.MqttSubscribeMessage
import io.zibi.codec.mqtt.MqttUnsubAckMessage
import io.zibi.codec.mqtt.MqttUnsubscribeMessage
import io.zibi.codec.mqtt.MqttVersion
import io.zibi.codec.mqtt.exception.DecoderException
import io.zibi.codec.mqtt.reasoncode.Disconnect
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.ByteBuffer

class MQTTHandler private constructor(
    private val connectionFactory: MQTTConnectionFactory,
    private val socket: Socket,
    private val useTasmota: Boolean,
    maxBytesInMessage: Int
) {
    private val dec = MqttDecoder(maxBytesInMessage = maxBytesInMessage)
    private val chunkBuffer = ChunkBuffer(ByteBuffer.allocate(dec.maxBytesInMessage))
    private var mqttConnection: MQTTConnection? = null
    private lateinit var mqttVersion: MqttVersion
    private var byteReadChannel: ByteReadChannel? = null
    private var byteWriteChannel: ByteWriteChannel? = null
    private var isNoServerShuttingDown = true
    private var socketJob: Job? = null

    init {
        socketJob = socket.launch {
            byteReadChannel = socket.openReadChannel()
            byteWriteChannel = socket.openWriteChannel(autoFlush = true)
            try {
                byteReadChannel?.awaitContent() ?: return@launch// wait for CONNECTION
                var message: MqttMessage = channelReadDecMessage()
                if (message is MqttConnectMessage) {
                    mqttVersion = MqttVersion.fromLevel(message.variableHeader().version)
                    mqttConnection = connectionFactory.create(mqttVersion, socket){ byteArray ->
                        writeChannel(byteArray) }
                } else {
                    throw DecoderException("Bad initialization connection")
                }
                channelRead(message)
                while (isActive) {
                    byteReadChannel?.awaitContent()
                    if(byteReadChannel?.isClosedForWrite == true) { //is socket close by client
                        if(byteReadChannel!!.availableForRead >= 2){ // is DISCONNECT
                            message = channelReadDecMessage( mqttVersion)
                            channelRead(message)
                        }
                        return@launch
                    }
                    message = channelReadDecMessage(mqttVersion)
                    channelRead(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: CancellationException) {
                e.printStackTrace()
            } finally {
                if(isNoServerShuttingDown)
                    connectionLost(socket)
            }
        }
    }

    private val mutex = Mutex()
    suspend fun readChannel(size: Int): ByteArray{
        while(byteReadChannel!!.availableForRead < size) delay(50)
        val byteArray = ByteArray(size)
        mutex.withLock {
            byteReadChannel!!.readFully(byteArray, 0, size)
        }
        return byteArray
    }

    suspend fun writeChannel(byteArray: ByteArray){
        mutex.withLock {
            byteWriteChannel!!.writeFully(byteArray)
        }
    }

    @Throws(Exception::class)
    suspend fun channelRead( msg: MqttMessage) {
        try {
            val newMsg = if( useTasmota && msg.fixedHeader.messageType == MqttMessageType.PUBLISH )
                changeRetainToTrue(msg as MqttPublishMessage)
            else
                msg
            mqttConnection?.handleMessage(newMsg)
        } catch (ex: Throwable) {
//            LOG.error("Error processing protocol message: {}", msg.fixedHeader.messageType, ex)
//            socket.channel().close()
//                .addListener(ChannelFutureListener { LOG.info("Closed client channel due to exception in processing") })
//        } finally {
//            ReferenceCountUtil.release(msg)
        }
    }

//    @Throws(Exception::class)
//    suspend fun channelReadComplete(socket: Socket){//socket: ChannelHandlerContext) {
//        val mqttConnection = NettyUtils.mqttConnection(HandlerContext.getAttr(socket))
//        mqttConnection.readCompleted()
//    }

//    private fun channelActive(socket: Socket) {
//        val connection = connectionFactory.create(socket)
//        NettyUtils.mqttConnection(HandlerContext.getAttr(socket), connection)
//    }

//    private suspend fun channelInactive(socket: Socket) {
//        mqttConnection?.handleConnectionLost()
//        removeConnection(socket)
//    }

//    fun exceptionCaught(socket: ChannelHandlerContext, cause: Throwable) {
//        LOG.error(
//            "Unexpected exception while processing MQTT message. Closing Netty channel. CId={}",
//            NettyUtils.clientID(socket.channel()), cause
//        )
//        socket.close().addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
//    }

//    suspend fun channelWritabilityChanged(socket: Socket) {
//        if (socket.channel().isWritable()) {
//            m_processor.notifyChannelWritable(socket.channel());
//        }
//        mqttConnection?.writabilityChanged()
//        socket.fireChannelWritabilityChanged()
//    }

//    suspend fun userEventTriggered(socket: ChannelHandlerContext, evt: Any) {
////        if (evt is ResendNotAckedPublishes) {
//            val mqttConnection = NettyUtils.mqttConnection(socket.channel())
//            mqttConnection.resendNotAckedPublishes()
////        }
////        socket.fireUserEventTriggered(evt)
//    }

    private fun changeRetainToTrue( msg: MqttPublishMessage): MqttPublishMessage{
        val topics = msg.variableHeader().topicName.split("RESULT")
        return if( topics.size == 2 && topics[0].startsWith("stat/")) {
            val newFixedHeader = MqttFixedHeader(
                msg.fixedHeader.messageType,
                msg.fixedHeader.isDup,
                MqttQoS.AT_LEAST_ONCE,
                true,
                msg.fixedHeader.remainingLength
            )
            MqttPublishMessage( newFixedHeader, msg.variableHeader(), msg.payload())
        }else{
            msg
        }
    }

    @Throws(Exception::class)
    suspend fun channelReadDecMessage( mqttVer: MqttVersion = MqttVersion.MQTT_3_1_1): MqttMessage {
        val mqttFixedHeader = dec.decodeFixedHeader(mqttVer){ size -> readChannel(size) }
        while (byteReadChannel!!.availableForRead < mqttFixedHeader.remainingLength)
            delay(10)
        chunkBuffer.reset()
        mutex.withLock {
            byteReadChannel?.readFully(chunkBuffer, mqttFixedHeader.remainingLength)
        }
        return when (mqttFixedHeader.messageType) {
            MqttMessageType.CONNACK -> MqttPubCompleteMessage(mqttFixedHeader,
                dec.decodePubCompleteVariableHeader(mqttVer,chunkBuffer)  )
            MqttMessageType.SUBACK -> MqttSubAckMessage(mqttFixedHeader,
                    dec.decodeVariableHeader(mqttVer,chunkBuffer),
                    dec.decodeSubAckPayload( chunkBuffer)  )
            MqttMessageType.UNSUBACK -> MqttUnsubAckMessage(mqttFixedHeader,
                    dec.decodeVariableHeader(mqttVer,chunkBuffer),
                    dec.decodeUnsubAckPayload( chunkBuffer)  )
            MqttMessageType.PUBLISH -> {
                val mqttPublishVariableHeader = dec.decodePublishVariableHeader(mqttVer,chunkBuffer,mqttFixedHeader)
                MqttPublishMessage(mqttFixedHeader,
                    mqttPublishVariableHeader,
                    dec.decodePublishPayload( chunkBuffer)
                )
            }
            // qos = 1 Ta odpowiedź jest wymagana do wydania
            MqttMessageType.PUBACK -> MqttPubAckMessage( mqttFixedHeader,
                    dec.decodeVariableHeader(mqttVer,chunkBuffer))
            MqttMessageType.PUBREC -> MqttPubReceivedMessage( mqttFixedHeader,
                dec.decodeVariableHeader(mqttVer,chunkBuffer))
            MqttMessageType.PUBREL -> MqttPubReleaseMessage( mqttFixedHeader,
                dec.decodeVariableHeader(mqttVer,chunkBuffer))
            MqttMessageType.PUBCOMP -> MqttPubCompleteMessage(
                mqttFixedHeader,dec.decodePubCompleteVariableHeader(mqttVer,chunkBuffer))
            MqttMessageType.PINGRESP -> MqttPingResponseMessage( mqttFixedHeader)
            MqttMessageType.CONNECT -> {
                val connectVarHeader = dec.decodeConnectionVariableHeader(chunkBuffer)
                MqttConnectMessage(mqttFixedHeader,
                    variableHeader = connectVarHeader,
                    payload = dec.decodeConnectionPayload(chunkBuffer,connectVarHeader))
            }
            MqttMessageType.SUBSCRIBE -> MqttSubscribeMessage(mqttFixedHeader,
                    variableHeader = dec.decodeVariableHeader(mqttVer, chunkBuffer),
                    payload = dec.decodeSubscribePayload(chunkBuffer))
            MqttMessageType.UNSUBSCRIBE -> MqttUnsubscribeMessage(mqttFixedHeader,
                    variableHeader = dec.decodeVariableHeader(mqttVer, chunkBuffer),
                    payload = dec.decodeUnsubscribePayload(chunkBuffer))
            MqttMessageType.PINGREQ -> MqttPingRequestMessage( mqttFixedHeader)
            MqttMessageType.DISCONNECT -> MqttMessage( mqttFixedHeader,null)
            MqttMessageType.AUTH -> MqttMessage( mqttFixedHeader,null)
        }
    }

    companion object {
        fun addConnection(connectionFactory: MQTTConnectionFactory, socket: Socket,
                          useTasmota: Boolean, maxBytesInMessage: Int): MQTTHandler {
            return if (mapMqttHandle.containsKey(socket)) {
                mapMqttHandle[socket]!!
            } else {
                val newAttr = MQTTHandler(connectionFactory, socket, useTasmota, maxBytesInMessage)
                mapMqttHandle[socket] = newAttr
                newAttr
            }
        }

        fun close(socket: Socket) {
            mapMqttHandle.remove(socket)
            socket.close()
        }

        suspend fun connectionLost(socket: Socket) {
            mapMqttHandle[socket]?.mqttConnection?.handleConnectionLost()
            mapMqttHandle.remove(socket)
        }

        fun serverShuttingDown(){
            mapMqttHandle.forEach { (socket, mqttHandle) ->
                socket.launch {
                    mqttHandle.isNoServerShuttingDown = false
                    mqttHandle.byteWriteChannel?.writeFully(
                        MqttDisconnectMessage(
                            disconnectReasonCode = Disconnect.SERVER_SHUTTING_DOWN,
                            mqttVersion = mqttHandle.mqttVersion
                        ).toDecByteArray(mqttHandle.mqttVersion)
                    )
//                    socket.close()
                    mqttHandle.socketJob?.cancelAndJoin()
                }
            }
            mapMqttHandle.clear()
        }

        private val mapMqttHandle = mutableMapOf<Socket, MQTTHandler>()

    }
}
