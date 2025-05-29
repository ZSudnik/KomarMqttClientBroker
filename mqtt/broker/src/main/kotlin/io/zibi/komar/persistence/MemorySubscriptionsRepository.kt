package io.zibi.komar.persistence

import io.zibi.komar.broker.ISubscriptionsRepository
import io.zibi.komar.broker.subscriptions.Subscription
import java.util.Collections
import java.util.concurrent.ConcurrentSkipListSet

class MemorySubscriptionsRepository : ISubscriptionsRepository {
    private val subscriptions: MutableSet<Subscription> = ConcurrentSkipListSet()
    override fun listAllSubscriptions(): Set<Subscription> {
        return Collections.unmodifiableSet(subscriptions)
    }

    override fun addNewSubscription(subscription: Subscription) {
        subscriptions.add(subscription)
    }

    override fun removeSubscription(topic: String, clientID: String) {
        subscriptions
            .filter { s: Subscription -> s.topicFilter.toString() == topic && s.clientId == clientID }
            .first()
            .let { o: Subscription -> subscriptions.remove(o) }
    }
}
