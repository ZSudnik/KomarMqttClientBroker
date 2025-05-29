package io.zibi.komar.mclient.core

import java.io.Serializable

class MessageData : Serializable {
    var topic: String = ""
    lateinit var payload: ByteArray
    var qos = 0
    var isRetained = false
    var isDup = false
    var messageId = 0
    val timestamp = System.currentTimeMillis()

    val stringId: String
        get() = messageId.toString()

    companion object {
        private const val serialVersionUID = 1L
    }
}