package com.zibi.service.broker.service

import com.zibi.service.broker.log.LogStream
import com.zibi.service.broker.log.LogData
import com.zibi.service.broker.log.LogType
import com.zibi.service.broker.log.MsgType
import io.zibi.komar.interception.AbstractInterceptHandler
import io.zibi.komar.interception.messages.*

class MQTTListener(private val logStream: LogStream) : AbstractInterceptHandler() {

    override fun getID(): String {
        return "MQTTServerListener"
    }

    override fun onServerNotification(msg: InterceptServerAppNotification) {
        val logMsg = "${timeStr()} APP: ${msg.reason}"
        log(logMsg, MsgType.MESSAGE)
    }

    override fun onConnect(msg: InterceptConnectMessage) {
        val logMsg = "${timeStr()} CON: ${msg.username} Connected"
        log(logMsg, MsgType.CONNECTION)
    }

    override fun onDisconnect(msg: InterceptDisconnectMessage) {
        val logMsg = "${timeStr()} DCO: ${msg.username} Disconnected"
        log(logMsg, MsgType.CONNECTION)
    }

    override fun onConnectionLost(msg: InterceptConnectionLostMessage) {
        val logMsg = "${timeStr()} CLO: ${msg.username} Connection Lost"
        log(logMsg, MsgType.CONNECTION)
    }

    override fun onPublish(msg: InterceptPublishMessage) {
        val logMsg = "${timeStr()} PUB: ${msg.username} : ${msg.topicName} : ${msg.payload}"
        log(logMsg, MsgType.MESSAGE)
    }

    override fun onSubscribe(msg: InterceptSubscribeMessage) {
        val logMsg = "${timeStr()} SUB: ${msg.username} : ${msg.topicFilter}"
        log(logMsg, MsgType.MESSAGE)
    }

    override fun onUnsubscribe(msg: InterceptUnsubscribeMessage) {
        val logMsg = "${timeStr()} USB: ${msg.username} : ${msg.topicFilter}"
        log(logMsg, MsgType.MESSAGE)
    }

    override fun onMessageAcknowledged(msg: InterceptAcknowledgedMessage) {
        val logMsg = "${timeStr()} ACK: ${msg.username} : ${msg.topic}"
        log(logMsg, MsgType.MESSAGE)
    }

//    override fun onSessionLoopError(error: Throwable) {}

    fun log(logMsg: String, msgType: MsgType) {
        logStream.addLog( LogData(logMsg, msgType, LogType.INFO))
    }

    companion object {
        const val TAG = "MQTTServerListener"
    }
}