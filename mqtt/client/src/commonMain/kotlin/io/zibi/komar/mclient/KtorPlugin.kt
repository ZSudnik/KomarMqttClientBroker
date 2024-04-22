package io.zibi.komar.mclient

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.events.EventDefinition
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.IListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class TopicContext(val topic: String, private val mqttClient: Mqtt){
    fun sendMessage(topic: String, msg: String) =
        mqttClient.publish(topic, msg)
}
typealias MessageListener = suspend TopicContext.(message: String) -> Unit

fun Application.Mqtt(config: Mqtt.Configuration.() -> Unit): Mqtt = install(Mqtt, config)

interface Mqtt : CoroutineScope , IMqttClient {

    class Configuration {
        var connectionOptions = MqttConnectOptions()
        var autoConnect: Boolean = true
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

    fun addTopicListener(topic: String, listener: MessageListener)
    fun addGeneralListener(listener: IListener)
    fun addOnOffMonitorConnection(onOff: (Boolean) -> Unit)

//    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, CustomHeader> {
//        // ...
//    }

    companion object Plugin : BaseApplicationPlugin<Application, Configuration, Mqtt> {
        override val key: AttributeKey<Mqtt> = AttributeKey("Mqtt")
        val ConnectedEvent: EventDefinition<Mqtt> = EventDefinition()
        val ClosedEvent: EventDefinition<Unit> = EventDefinition()
        private val coroutineContext = CoroutineScope(Dispatchers.IO).coroutineContext
        lateinit var client: MqttClientPlugin


        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Mqtt {
            val applicationMonitor = pipeline.environment.monitor

            val config = Configuration().apply(configure)
            val delegate = MqttClient(config.connectionOptions, coroutineContext)
            client = MqttClientPlugin(config, coroutineContext, delegate)

            if (config.autoConnect) client.connectAuto(null).also {
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
        private val config: Mqtt.Configuration,
        private val corContext: CoroutineContext,
        private val mqttClient: MqttClient,
    ) : Mqtt {

        override val coroutineContext: CoroutineContext
            get() = corContext

        override fun connectAuto(timeout: Long?) {
            mqttClient.connectAuto(timeout)
            mqttClient.onMessageArrived = this::onMessageArrived
        }
        override fun connectOne(timeout: Long?) {
            mqttClient.connectOne(timeout)
            mqttClient.onMessageArrived = this::onMessageArrived
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

        private val messageListenerByTopic = ConcurrentHashMap<String, MessageListener>()

        override fun addTopicListener(topic: String, listener: MessageListener) {
            messageListenerByTopic[topic] = listener
        }

        override fun addGeneralListener(listener: IListener) {
            mqttClient.listener = listener
        }

        override fun addOnOffMonitorConnection(onOff: (Boolean) -> Unit) {
            mqttClient.stateConnection = onOff
        }


        private fun onMessageArrived(topic: String, msg: String){
            messageListenerByTopic[topic].let {
                launch {
                    it?.invoke(TopicContext(topic, this@MqttClientPlugin), msg)
                }
            }
        }


    }
}

@KtorDsl
fun Route.topic(topic: String, qos: MqttQoS = MqttQoS.AT_MOST_ONCE, listener: MessageListener): Job {
    val client = application.plugin(Mqtt)
    client.addTopicListener(topic, listener)
    return client.launch {
        client.subscribe(  qos.value(), listOf( topic) )
    }
}
