package io.zibi.komar.mclient.utils

object Log {
    fun i(msg: String) {
        println(msg)
    }
    fun e(msg: String) {
        System.err.println(msg)
    }
}