package io.zibi.komar.broker

import io.zibi.komar.broker.subscriptions.Subscription

interface ISubscriptionsRepository {
    fun listAllSubscriptions(): Set<Subscription>
    fun addNewSubscription(subscription: Subscription)
    fun removeSubscription(topic: String, clientID: String)
}
