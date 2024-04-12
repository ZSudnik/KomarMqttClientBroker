package io.zibi.komar.mclient.core

interface IListener {
    fun onConnected()
    fun onConnectFailed(e: Throwable)
    fun onConnectLost(e: Throwable)
    fun onReconnectStart(cur: Int)
    fun onMessageArrived(topic: String, s: String)
}