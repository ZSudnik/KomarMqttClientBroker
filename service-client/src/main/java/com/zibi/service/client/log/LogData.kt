package com.zibi.service.client.log

data class LogData(
    val msg: String,
    val msgType: MsgType = MsgType.INTERNAL,
    val logType: LogType = LogType.INFO
)

enum class LogType {
    INFO,
    WARNING,
    ERROR
}

enum class MsgType {
    CONNECTION,
    MESSAGE,
    INTERNAL
}