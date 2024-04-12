package io.zibi.komar.interception

import io.zibi.komar.interception.messages.InterceptAcknowledgedMessage
import io.zibi.komar.interception.messages.InterceptConnectMessage
import io.zibi.komar.interception.messages.InterceptConnectionLostMessage
import io.zibi.komar.interception.messages.InterceptDisconnectMessage
import io.zibi.komar.interception.messages.InterceptPublishMessage
import io.zibi.komar.interception.messages.InterceptSubscribeMessage
import io.zibi.komar.interception.messages.InterceptUnsubscribeMessage

/**
 * Basic abstract class usefull to avoid empty methods creation in subclasses.
 */
abstract class AbstractInterceptHandler : InterceptHandler {
    override fun getInterceptedMessageTypes(): Array<Class<*>> {
        return InterceptHandler.ALL_MESSAGE_TYPES
    }

    override fun onConnect(msg: InterceptConnectMessage) {}
    override fun onDisconnect(msg: InterceptDisconnectMessage) {}
    override fun onConnectionLost(msg: InterceptConnectionLostMessage) {}
    override fun onPublish(msg: InterceptPublishMessage) {
//        msg.payload.release() TODO ???
    }

    override fun onSubscribe(msg: InterceptSubscribeMessage) {}
    override fun onUnsubscribe(msg: InterceptUnsubscribeMessage) {}
    override fun onMessageAcknowledged(msg: InterceptAcknowledgedMessage) {}
}
