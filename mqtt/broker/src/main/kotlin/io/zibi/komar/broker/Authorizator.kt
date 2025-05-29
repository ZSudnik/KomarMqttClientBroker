package io.zibi.komar.broker

import io.zibi.komar.broker.security.IAuthorizatorPolicy
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttQoS
import io.zibi.codec.mqtt.MqttSubscribeMessage
import io.zibi.codec.mqtt.MqttTopicSubscription
import org.slf4j.LoggerFactory

class Authorizator(private val policy: IAuthorizatorPolicy) {
    /**
     * @param clientID
     * the clientID
     * @param username
     * the username
     * @param msg
     * the subscribe message to verify
     * @return the list of verified topics for the given subscribe message.
     */
    fun verifyTopicsReadAccess(
        clientID: String,
        username: String,
        msg: MqttSubscribeMessage
    ): List<MqttTopicSubscription> {
        val ackTopics: MutableList<MqttTopicSubscription> = mutableListOf()
        val messageId = Utils.messageId(msg)
        for (req in msg.payload().topicSubscriptions) {
            val topic = Topic(req.topicFilter)
            if (!policy.canRead(topic, username, clientID)) {
                // send SUBACK with 0x80, the user hasn't credentials to read the topic
                LOG.warn(
                    "Client does not have read permissions on the topic username: {}, messageId: {}, " +
                            "topic: {}", username, messageId, topic
                )
                ackTopics.add(MqttTopicSubscription(topic.toString(), MqttQoS.FAILURE))
            } else {
                val qos: MqttQoS = if (topic.isValid()) {
                    LOG.debug(
                        "Client will be subscribed to the topic username: {}, messageId: {}, topic: {}",
                        username, messageId, topic
                    )
                    req.qualityOfService()
                } else {
                    LOG.warn(
                        "Topic filter is not valid username: {}, messageId: {}, topic: {}",
                        username, messageId, topic
                    )
                    MqttQoS.FAILURE
                }
                ackTopics.add(MqttTopicSubscription(topic.toString(), qos))
            }
        }
        return ackTopics
    }

    /**
     * Ask the authorization policy if the topic can be used in a publish.
     *
     * @param topic
     * the topic to write to.
     * @param user
     * the user
     * @param client
     * the client
     * @return true if the user from client can publish data on topic.
     */
    fun canWrite(topic: Topic, user: String, client: String): Boolean {
        return policy.canWrite(topic, user, client)
    }

    fun canRead(topic: Topic, user: String, client: String): Boolean {
        return policy.canRead(topic, user, client)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Authorizator::class.java)
    }
}
