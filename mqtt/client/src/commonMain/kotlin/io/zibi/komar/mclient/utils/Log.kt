package io.zibi.komar.mclient.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Log {
    private var f = SimpleDateFormat("MM-dd HH:mm:ss.SSS",Locale.US)
    private var isEnable = true
    private var isEnablePing = false
    fun enable(b: Boolean) {
        isEnable = b
    }

    fun enablePing(b: Boolean) {
        isEnablePing = b
    }

    fun i(msg: String) {
        if (!isEnable) return
        if (!isEnablePing && msg.contains("[ping]")) return
        println(msg)
    }
    fun e(msg: String) {
        if (!isEnable) return
        if (!isEnablePing && msg.contains("[ping]")) return
        System.err.println(msg)
    }

    fun ix(msg: String) {
        if (!isEnable) return
        if (!isEnablePing && msg.contains("[ping]")) return
        println(
            f.format(Date()) + "-" + Thread.currentThread().id + " I/" + format(msg,3)
        )
    }

    fun ex(msg: String) {
        if (!isEnable) return
        if (!isEnablePing && msg.contains("[ping]")) return
        System.err.println(
            f.format(Date()) + "-" + Thread.currentThread().id + " I/" +
                    format(msg, 3)
        )
    }

    private fun format(message: String, stackTraceIndex: Int): String {
        val stackTrace = Thread.currentThread().stackTrace
        val fullClassName = Thread.currentThread().stackTrace[3].className
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        val methodName = Thread.currentThread().stackTrace[3].methodName
        val fileName = Thread.currentThread().stackTrace[3].fileName
        val lineNumber = Thread.currentThread().stackTrace[3].lineNumber
        val depth = Math.min(stackTrace.size - 1, stackTraceIndex)
        return String.format(
            Locale.getDefault(), "%s.%s(%s:%d): \n%s %s" + if (message.isNotEmpty()) "\n" else "",
            className, methodName, fileName, lineNumber, message, depth.toString()
        )
    }


}