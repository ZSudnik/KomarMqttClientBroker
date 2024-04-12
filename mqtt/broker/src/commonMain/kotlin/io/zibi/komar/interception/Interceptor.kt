package io.zibi.komar.interception

import io.zibi.komar.broker.subscriptions.Subscription
import io.zibi.komar.interception.messages.InterceptAcknowledgedMessage
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttPublishMessage

/**
 * This interface is to be used internally by the broker components.
 *
 *
 * An interface is used instead of a class to allow more flexibility in changing an implementation.
 *
 *
 * Interceptor implementations forward notifications to a `InterceptHandler`, that is
 * normally a field. So, the implementations should act as a proxy to a custom intercept handler.
 *
 * @see InterceptHandler
 */
interface Interceptor {
    fun notifyClientConnected(msg: MqttConnectMessage)
    fun notifyClientDisconnected(clientID: String, username: String)
    fun notifyClientConnectionLost(clientID: String, username: String)
    fun notifyTopicPublished(msg: MqttPublishMessage, clientID: String, username: String)
    fun notifyTopicSubscribed(sub: Subscription, username: String)
    fun notifyTopicUnsubscribed(topic: String, clientID: String, username: String)
    fun notifyMessageAcknowledged(msg: InterceptAcknowledgedMessage)
    fun notifyServerShuttingDown(reason: String)
    fun notifyServerStarting(reason: String)
    fun addInterceptHandler(interceptHandler: InterceptHandler)
    fun removeInterceptHandler(interceptHandler: InterceptHandler)
}
