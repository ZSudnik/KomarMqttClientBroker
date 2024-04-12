package io.zibi.komar.mclient.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.BaseApplicationPlugin
//import io.ktor.server.application.EventDefinition
import io.ktor.events.EventDefinition
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.util.AttributeKey
import io.zibi.komar.mclient.MqttClient
import io.zibi.komar.mclient.core.IListener
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.util.MqttConnectOptions
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
//import org.eclipse.paho.mqttv5.client.IMqttAsyncClient
//import org.eclipse.paho.mqttv5.client.IMqttToken
//import org.eclipse.paho.mqttv5.client.MqttActionListener
//import org.eclipse.paho.mqttv5.client.MqttCallback
//import org.eclipse.paho.mqttv5.client.MqttClientPersistence
//import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
//import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse
//import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
//import org.eclipse.paho.mqttv5.common.MqttException
//import org.eclipse.paho.mqttv5.common.MqttMessage
//import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
//import org.eclipse.paho.mqttv5.client.MqttAsyncClient as PahoMqttClient

fun Application.Mqtt(config: Mqtt.Configuration.() -> Unit) = //Mqtt.Configuration.() -> Unit) =
    install(Mqtt, config)

interface Mqtt : CoroutineScope {//, io.zibi.komar.mclient.IMqttClient { //IMqttAsyncClient {

    class Configuration {
        var connectionOptions = MqttConnectOptions()
//        var persistence: MqttClientPersistence = MemoryPersistence()
//        var broker: String = "1883"//""tcp://localhost:1883"
//        var clientId = "zibi_client"
        var autoConnect: Boolean = false
        val initialSubscriptions = listOf<TopicSubscription>()

        fun initialSubscriptions( subscription: List<TopicSubscription>) {
            initialSubscriptions + subscription
        }

        fun connectionOptions(configure: MqttConnectOptions.() -> Unit) {
            connectionOptions.apply(configure)
        }
    }

    suspend fun shutdown()
    fun addTopicListener(topic: Topic, listener: MessageListener)

    companion object Plugin : BaseApplicationPlugin<Application, Configuration, Mqtt> {
        override val key: AttributeKey<Mqtt> = AttributeKey("Mqtt")
        val ConnectedEvent: EventDefinition<Mqtt> = EventDefinition()
        val ClosedEvent: EventDefinition<Unit> = EventDefinition()

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Mqtt {
            val applicationMonitor = pipeline.environment.monitor

            val config = Configuration().apply(configure)
            val logger = pipeline.log
//            val delegate: IMqttAsyncClient = PahoMqttClient(config.broker, config.clientId, config.persistence)
//            val delegate = MqttClient(config.connectionOptions, )
            val listener = object :IListener{
                override fun onConnected() {}
                override fun onConnectFailed(e: Throwable) {}
                override fun onConnectLost(e: Throwable) {}
                override fun onReconnectStart(cur: Int) {}
                override fun onMessageArrived(topic: String, s: String) {}
            }
//            val client = MqttClientPlugin(config, logger, delegate, listener)
            val client = MqttClientPlugin(config, logger,  listener)

//            if (config.autoConnect) client.connectToBroker().also { applicationMonitor.raise(
            if (config.autoConnect) client.connectToBroker().also { applicationMonitor.raise(
                ConnectedEvent, client) }

            applicationMonitor.subscribe(ApplicationStopPreparing) {
//                client.shutdown()
//                client.close()
                it.monitor.raise(ClosedEvent, Unit)
            }

            return client
        }
    }
}

internal class MqttClientPlugin(
    private val config: Mqtt.Configuration,
    private val logger: Logger,
//    delegate: io.zibi.komar.mclient.IMqttClient, //IMqttAsyncClient
    private val listener: IListener
) : Mqtt {//, IListener by listener {
//    , io.zibi.komar.mclient.IMqttClient by delegate { //IMqttAsyncClient by delegate {

    private val parent: CompletableJob = Job()
    override val coroutineContext: CoroutineContext
        get() = parent

    private val messageListenerByTopic = ConcurrentHashMap<Topic, MessageListener>()

    fun connectToBroker() {
        object : IListener{
            override fun onConnected() {}
            override fun onConnectFailed(e: Throwable) {  }
            override fun onConnectLost(e: Throwable) {  }
            override fun onReconnectStart(cur: Int) {  }
            override fun onMessageArrived(topic: String, s: String) { }

        }
//        setCallback(
//            object : MqttCallback {
//                override fun connectComplete(reconnect: Boolean, serverURI: String) =
//                    logger.info("connected to broker: $serverURI").also {
//                        config.initialSubscriptions.forEach { subscription ->
//                            subscribe(
//                                subscription.qualityOfService.value(),
//                                listOf(subscription.topic.value)
//                            ).actionCallback = object : MqttActionListener {
//                                override fun onSuccess(asyncActionToken: IMqttToken) =
//                                    logger.info("successfully subscribed to topic: [ ${subscription.topic.value} ] with qos: [ ${subscription.qualityOfService.level} ]")
//
//                                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) =
//                                    logger.error("could not subscribeTo to topic: [ ${subscription.topic.value} ] due to: [ ${exception.message} ]")
//                            }
//                        }
//                    }
//
//                override fun authPacketArrived(reasonCode: Int, properties: Auth) {
//                    logger.debug("Auth reason code: [ $reasonCode ]  with reason: [ ${properties.name} ]")
//                }
//
//                override fun disconnected(disconnectResponse: Disconnect) {
//                    logger.warn("disconnected from broker due to: [ ${disconnectResponse.name} ]")
//                }
//
////                override fun mqttErrorOccurred(exception: MqttException) {
////                    logger.error("an error occurred: [ ${exception.message} ]")
////                }
//
//                override fun messageArrived(topic: String, message: MqttMessage) {
//                    val validTopic = Topic(topic)
//                    messageListenerByTopic[validTopic]?.let {
//                        launch {
//                            it.invoke(TopicContext(validTopic, this@MqttClientPlugin), message)
//                        }
//                    }
//                    logger.debug("received ${message.toString()} from topic [ $topic ]")
//                }
//
////                override fun deliveryComplete(token: IMqttToken) {
////                    logger.debug("delivered message ${String(token.message.payload)} ")
////                }
//            }
//        )
//        this.connect()
//        connect(config.connectionOptions).waitForCompletion()
    }

    suspend fun publishMessageTo(
        topic: Topic,
        msg: String,
        qos: MqttQoS,
        retained: Boolean
    ) {
//        val message = MqttMessage()
//        message.payload = msg.toByteArray()
//        publish(topic.value, message.payload, qos.level, retained).await()
//        this.publish(topic.value, msg)

    }

    override fun addTopicListener(topic: Topic, listener: MessageListener) {
        messageListenerByTopic[topic] = listener
    }

//    suspend fun unsubscribeFrom(topic: Topic) =
//        if (messageListenerByTopic[topic] == null)
//            error("Cannot unsubscribe from non existing Subscription of topic: [ $topic ]")
//        else {
//            this.unsubscribe(listOf( topic.value))
////            unsubscribe(topic.value)
////                .await()
//                .also { messageListenerByTopic.remove(topic) }
//        }

    override suspend fun shutdown() {
        logger.info("shutting down Mqtt")
        parent.complete()
        messageListenerByTopic.clear()
//        this.close()
//        disconnectForcibly()
    }
}
