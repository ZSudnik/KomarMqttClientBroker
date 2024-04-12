package io.zibi.komar.persistence

import io.zibi.komar.broker.ISubscriptionsRepository
import io.zibi.komar.broker.subscriptions.Subscription
import org.h2.mvstore.MVMap
import org.h2.mvstore.MVStore
import org.slf4j.LoggerFactory

class H2SubscriptionsRepository internal constructor(mvStore: MVStore) : ISubscriptionsRepository {
    private val subscriptions: MVMap<String?, Subscription>

    init {
        subscriptions = mvStore.openMap(SUBSCRIPTIONS_MAP)
    }

    override fun listAllSubscriptions(): Set<Subscription> {
        LOG.debug("Retrieving existing subscriptions")
        val results: MutableSet<Subscription> = HashSet()
        val mapCursor = subscriptions.cursor(null)
        while (mapCursor.hasNext()) {
            mapCursor.next()
            results.add(mapCursor.value)
        }
        LOG.debug("Loaded {} subscriptions", results.size)
        return results
    }

    override fun addNewSubscription(subscription: Subscription) {
        subscriptions[subscription.topicFilter.toString() + "-" + subscription.clientId] =
            subscription
    }

    override fun removeSubscription(topic: String, clientID: String) {
        subscriptions.remove("$topic-$clientID")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(
            H2SubscriptionsRepository::class.java
        )
        private const val SUBSCRIPTIONS_MAP = "subscriptions"
    }
}
