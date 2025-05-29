package io.zibi.komar.broker.subscriptions

import io.zibi.codec.mqtt.MqttQoS
import java.io.Serializable

/**
 * Maintain the information about which Topic a certain ClientID is subscribed and at which QoS
 */
class Subscription(
    val clientId: String,
    val topicFilter: Topic,
    val requestedQos: MqttQoS,
) : Serializable, Comparable<Subscription> {

    constructor(orig: Subscription): this(
        clientId = orig.clientId,
        topicFilter = orig.topicFilter,
        requestedQos = orig.requestedQos,
    )

    fun qosLessThan(sub: Subscription): Boolean {
        return requestedQos.value() < sub.requestedQos.value()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that: Subscription = other as Subscription
        if (clientId != that.clientId) return false
        return topicFilter == that.topicFilter
    }

    override fun hashCode(): Int {
        var result: Int = clientId.hashCode()
        result = 31 * result + topicFilter.hashCode()
        return result
    }

    override fun toString(): String {
        return String.format(
            "[filter:%s, clientID: %s, qos: %s]",
            topicFilter,
            clientId,
            requestedQos
        )
    }

//    override fun clone(): Subscription? {
//        try {
//            return super.clone() as Subscription?
//        } catch (e: CloneNotSupportedException) {
//            return null
//        }
//    }

    override operator fun compareTo(other: Subscription): Int {
        val compare: Int = clientId.compareTo((other.clientId))
        if (compare != 0) {
            return compare
        }
        return topicFilter.compareTo(other.topicFilter)
    }

    companion object {
        private val serialVersionUID: Long = -3383457629635732794L
    }
}
