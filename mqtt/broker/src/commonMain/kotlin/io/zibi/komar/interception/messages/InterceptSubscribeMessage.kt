package io.zibi.komar.interception.messages

import io.zibi.komar.broker.subscriptions.Subscription
import io.zibi.codec.mqtt.MqttQoS

class InterceptSubscribeMessage(private val subscription: Subscription, val username: String) :
    InterceptMessage {

    val clientID: String
        get() = subscription.clientId
    val requestedQos: MqttQoS
        get() = subscription.requestedQos
    val topicFilter: String
        get() = subscription.topicFilter.toString()
}
