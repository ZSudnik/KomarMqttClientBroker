package com.zibi.service.broker.log

import com.zibi.service.broker.service.MQTTService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onSubscription

interface LogStream{
    val logFlow: SharedFlow<LogData>
    fun addLog(logData: LogData)
    fun getAllLogs(): List<String>
    fun clear()
}

class LogStreamImp : LogStream{

    private val logsCache = mutableListOf<String>()

    private val _logFlow = MutableSharedFlow<LogData>(
        extraBufferCapacity = 1000,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val logFlow: SharedFlow<LogData>
        get() {
            return _logFlow
        }

    override fun addLog(logData: LogData) {
        if( logsCache.size >= 300) logsCache.removeFirst()
        logsCache.add(logData.msg)

        scope.launch {
            _logFlow.emit(logData)
        }
    }

    override fun getAllLogs(): List<String> {
        return logsCache
    }

    override fun clear() {
        scope.coroutineContext.cancelChildren()
    }
}