package io.zibi.komar.mclient.core

interface IListener {
    fun onConnected()
    fun onSubscribe(topic: String)
    fun onUnsubscribe(topic: String)
    fun onMessageArrived(topic: String, s: String)
    fun onDisConnected(description: String)
    fun onConnectFailed(e: Throwable)
    fun onConnectLost(e: Throwable)
    fun onResponseTimeout(description: String)
}