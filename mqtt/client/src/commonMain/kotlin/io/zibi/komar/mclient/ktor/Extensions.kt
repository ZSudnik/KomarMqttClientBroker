package io.zibi.komar.mclient.ktor

//import io.ktor.server.application.feature
//import io.ktor.server.plugins.f
import io.ktor.server.application.plugin
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.zibi.komar.mclient.core.IListener
//import io.ktor.util.pipeline.ContextDsl

import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttQoS.AT_MOST_ONCE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//import org.eclipse.paho.mqttv5.client.IMqttMessageListener
//import org.eclipse.paho.mqttv5.client.IMqttToken
//import org.eclipse.paho.mqttv5.client.MqttActionListener
//import org.eclipse.paho.mqttv5.common.MqttMessage
//import org.eclipse.paho.mqttv5.common.MqttSubscription
//import org.eclipse.paho.mqttv5.common.packet.MqttProperties

@OptIn(ExperimentalCoroutinesApi::class)
//suspend fun IMqttToken.await(): IMqttToken =
//    suspendCancellableCoroutine { cont ->
//        actionCallback = object : MqttActionListener {
//            override fun onSuccess(asyncActionToken: IMqttToken) {
//                cont.resume(asyncActionToken, null)
//            }
//
//            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                cont.resumeWithException(exception)
//            }
//        }
//    }

class TopicContext(val topic: Topic, private val mqttClient: Mqtt) {
//    fun unsubscribe() = mqttClient.unsubscribe(listOf( topic.value))
//    fun sendMessage(topic: String, msg: String, qos: MqttQoS = AT_MOST_ONCE, retained: Boolean = true) =
//        mqttClient.publish(topic, msg)
//        mqttClient.publish(topic, MqttMessage(msg.toByteArray(), qos.level, retained, MqttProperties())).await()
}

//@ContextDsl
fun Route.topic(topic: String, qos: MqttQoS = AT_MOST_ONCE, listener: MessageListener): Job {
    val client = application.plugin(Mqtt)
    val validTopic = Topic(topic)
    return client.launch {
//        client.subscribe( qos.value(), listOf( validTopic.value))
        client.addTopicListener(validTopic, listener)
    }
}

fun Mqtt.publishMessageTo(
    topic: Topic,
    msg: String,
    qos: MqttQoS,
    retained: Boolean
) {
//    val message = MqttMessage()
//    message.payload = msg.toByteArray()
//    publish(topic.value, message.payload, qos.level, retained).await()
//    this.publish(topic.value, msg)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun Mqtt.subscribeTo(topic: Topic, qos: MqttQoS, listener: IListener) {
//    subscribe( qos.value(), listOf( topic.value))
}

//fun Mqtt.unsubscribeFrom(topic: Topic) = unsubscribe(listOf( topic.value))
