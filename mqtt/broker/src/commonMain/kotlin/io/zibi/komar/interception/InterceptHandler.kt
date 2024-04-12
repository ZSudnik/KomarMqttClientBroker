package io.zibi.komar.interception

import io.zibi.komar.interception.messages.InterceptAcknowledgedMessage
import io.zibi.komar.interception.messages.InterceptConnectMessage
import io.zibi.komar.interception.messages.InterceptConnectionLostMessage
import io.zibi.komar.interception.messages.InterceptDisconnectMessage
import io.zibi.komar.interception.messages.InterceptPublishMessage
import io.zibi.komar.interception.messages.InterceptServerAppNotification
import io.zibi.komar.interception.messages.InterceptSubscribeMessage
import io.zibi.komar.interception.messages.InterceptUnsubscribeMessage

/**
 * This interface is used to inject code for intercepting broker events.
 *
 *
 * The events can act only as observers.
 *
 *
 * Almost every method receives a subclass of [MqttMessage], except `onDisconnect`
 * that receives the client id string and `onSubscribe` and `onUnsubscribe`
 * that receive a [Subscription] object.
 */
interface InterceptHandler {
    /**
     * @return the identifier of this intercept handler.
     */
    fun getID(): String

    /**
     * @return the InterceptMessage subtypes that this handler can process. If the result is null or
     * equal to ALL_MESSAGE_TYPES, all the message types will be processed.
     */
    fun getInterceptedMessageTypes(): Array<Class<*>>
    fun onConnect(msg: InterceptConnectMessage)
    fun onDisconnect(msg: InterceptDisconnectMessage)
    fun onConnectionLost(msg: InterceptConnectionLostMessage)
    fun onServerNotification(msg: InterceptServerAppNotification)

    /**
     * Called when a message is published. The receiver MUST release the payload of the message, either
     * by calling super.onPublish, or by calling msg.getPayload.release() directly.
     *
     * @param msg The message that was published.
     */
    fun onPublish(msg: InterceptPublishMessage)
    fun onSubscribe(msg: InterceptSubscribeMessage)
    fun onUnsubscribe(msg: InterceptUnsubscribeMessage)
    fun onMessageAcknowledged(msg: InterceptAcknowledgedMessage)

    companion object {
        val ALL_MESSAGE_TYPES = arrayOf<Class<*>>(
            InterceptConnectMessage::class.java,
            InterceptDisconnectMessage::class.java,
            InterceptConnectionLostMessage::class.java,
            InterceptPublishMessage::class.java,
            InterceptSubscribeMessage::class.java,
            InterceptUnsubscribeMessage::class.java,
            InterceptAcknowledgedMessage::class.java,
            InterceptServerAppNotification::class.java
        )
    }
}
