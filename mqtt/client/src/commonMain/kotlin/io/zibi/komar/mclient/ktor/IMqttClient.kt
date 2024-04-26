package io.zibi.komar.mclient.ktor

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface IMqttClient {


    @Throws(Exception::class)
    fun connectAuto()

    fun connectOne()

    @Throws(Exception::class)
    fun subscribe(topics: List<String>)

    @Throws(Exception::class)
    fun subscribe(qos: Int, topics: List<String>)

    @Throws(Exception::class)
    fun unsubscribe(topics: List<String>)

    @Throws(Exception::class)
    fun publish(topic: String, content: String)

    fun shutDown()

    fun disConnect()
}