package io.zibi.komar.mclient.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.events.EventDefinition
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.ktor.IMqttClient
import io.zibi.komar.mclient.ktor.MqttClient
import io.zibi.komar.mclient.core.IListener
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KSuspendFunction2

class TopicContext(val topic: String, private val mqttClient: Mqtt){
    fun sendMessage(topic: String, msg: String) =
        mqttClient.publish(topic, msg)
}
typealias MessageListener = suspend TopicContext.(message: String) -> Unit

fun Application.Mqtt(config: Mqtt.Configuration.() -> Unit): Mqtt = install(Mqtt, config)

//fun Application.module() { configureMqtt() }

interface Mqtt : CoroutineScope , IMqttClient {

    class Configuration {
        var connectionOptions = MqttConnectOptions()
        val topicSubscription = listOf<String>()
        var qoSSubscription: Int = 0
        fun initSubscriptions(qos: Int = 1, topics: List<String>) {
            qoSSubscription = qos
            topicSubscription + topics
        }

        fun connectionOptions(configure: MqttConnectOptions.() -> Unit) {
            connectionOptions.apply(configure)
        }
    }

    fun isConnected(): Boolean

    fun addTopicListener(topic: String, listener: MessageListener)
//    fun addGeneralListener(listener: IListener)
    fun addOnMessageArrived(onMsgArrived: KSuspendFunction2<String, String, Unit>)
    fun addOnOffMonitorConnection(onOff: (Boolean) -> Unit)
    fun reloadConfiguration(connectionOptions: MqttConnectOptions)

    //    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
    companion object Plugin : BaseApplicationPlugin<Application, Configuration, Mqtt> {
        override val key: AttributeKey<Mqtt> = AttributeKey("Mqtt")
        val ConnectedEvent: EventDefinition<Mqtt> = EventDefinition()
        val ClosedEvent: EventDefinition<Unit> = EventDefinition()
//        private val coroutineContext = CoroutineScope(Dispatchers.IO).coroutineContext
        lateinit var client: MqttClientPlugin


        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Mqtt {
            val applicationMonitor = pipeline.environment.monitor

            val config = Configuration().apply(configure)
            val parent: CompletableJob = Job()
            val mqttClient = MqttClient(config.connectionOptions, parent)
            client = MqttClientPlugin(mqttClient, parent)

            client.connectAuto().also {
                applicationMonitor.raise(
                    ConnectedEvent, client
                )
            }

            applicationMonitor.subscribe(ApplicationStopPreparing) {
                client.shutDown()
                it.monitor.raise(ClosedEvent, Unit)
            }

            return client
        }
    }


    class MqttClientPlugin(
        private val mqttClient: MqttClient,
        private val parent: CompletableJob
    ) : Mqtt {

        override val coroutineContext: CoroutineContext
            get() = parent

        override fun isConnected(): Boolean = mqttClient.isConnected

        override fun connectAuto() {
            addOnMessageArrived(this::onMessageArrived)
            mqttClient.connectAuto()
        }

        override fun connectOne() {
            addOnMessageArrived(this::onMessageArrived)
            mqttClient.connectOne()
        }

        override fun subscribe(topics: List<String>) {
            mqttClient.subscribe(topics)
        }

        override fun subscribe(qos: Int, topics: List<String>) {
            mqttClient.subscribe(qos, topics)
        }

        override fun unsubscribe(topics: List<String>) {
            mqttClient.unsubscribe(topics)
            topics.forEach { topic ->
                messageListenerByTopic.remove(topic)
            }
        }

        override fun publish(topic: String, content: String) {
            mqttClient.publish(topic, content)
        }

        override fun shutDown() {
            mqttClient.shutDown()
            coroutineContext.cancel()
            messageListenerByTopic.clear()
//        this.close()
//        disconnectForcibly()
        }

        override fun disConnect() {
            mqttClient.disConnect()
        }

        private val messageListenerByTopic = ConcurrentHashMap<String, MessageListener>()

        override fun addTopicListener(topic: String, listener: MessageListener) {
            messageListenerByTopic[topic] = listener
        }

        override fun addOnMessageArrived(onMsgArrived: KSuspendFunction2<String, String, Unit>) {
            mqttClient.listMessageArrived.add(onMsgArrived)
        }

        override fun addOnOffMonitorConnection(onOff: (Boolean) -> Unit) {
            mqttClient.stateConnection = onOff
        }

        override fun reloadConfiguration(connectionOptions: MqttConnectOptions) {
            mqttClient.reloadConfiguration(connectionOptions)
        }

        private suspend fun onMessageArrived(topic: String, msg: String) {
            messageListenerByTopic[topic].let { messageListener ->
                launch {
                    messageListener?.invoke(TopicContext(topic, this@MqttClientPlugin), msg)
                }
            }
        }
    }

}

@KtorDsl
fun Route.topic(topic: String, qos: MqttQoS = MqttQoS.AT_MOST_ONCE, listener: MessageListener) {//}: Job {
    val client = application.plugin(Mqtt)
    client.addTopicListener(topic, listener)
//    return client.launch {
//        client.subscribe(  qos.value(), listOf( topic) )
//    }
}
