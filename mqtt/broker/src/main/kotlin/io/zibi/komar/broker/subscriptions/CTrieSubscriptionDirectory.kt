package io.zibi.komar.broker.subscriptions

import io.zibi.komar.broker.ISubscriptionsRepository
import org.slf4j.LoggerFactory
import java.util.Optional
import kotlin.concurrent.Volatile

class CTrieSubscriptionDirectory(
    @Volatile
    private var subscriptionsRepository: ISubscriptionsRepository
) : ISubscriptionsDirectory {
    private val ctrie: CTrie = CTrie()

    init {
        if (LOG.isTraceEnabled) {
            LOG.trace("Reloading all stored subscriptions. SubscriptionTree = {}", dumpTree())
        }
        for (subscription in this.subscriptionsRepository.listAllSubscriptions()) {
            LOG.debug("Re-subscribing {}", subscription)
            ctrie.addToTree(subscription)
        }
        if (LOG.isTraceEnabled) {
            LOG.trace("Stored subscriptions have been reloaded. SubscriptionTree = {}", dumpTree())
        }
    }

    /**
     * @return the list of client ids that has a subscription stored.
     */
    override fun listAllSessionIds(): Set<String> {
        val subscriptions = subscriptionsRepository.listAllSubscriptions()
        val clientIds: MutableSet<String> = HashSet(subscriptions.size)
        for (subscription in subscriptions) {
            clientIds.add(subscription.clientId)
        }
        return clientIds
    }

    fun lookup(topic: Topic): Optional<CNode> {
        return ctrie.lookup(topic)
    }

    /**
     * Given a topic string return the clients subscriptions that matches it. Topic string can't
     * contain character # and + because they are reserved to listeners subscriptions, and not topic
     * publishing.
     * @param topic
     * to use fo searching matching subscriptions.
     * @return the list of matching subscriptions, or empty if not matching.
     */
    override fun matchWithoutQosSharpening(topic: Topic): List<Subscription> {
        return ctrie.recursiveMatch(topic)
    }

    override fun matchQosSharpening(topic: Topic): List<Subscription> {
        val subscriptions = matchWithoutQosSharpening(topic)
        val subsGroupedByClient: MutableMap<String, Subscription> = mutableMapOf()
        for (sub in subscriptions) {
            val existingSub = subsGroupedByClient[sub.clientId]
            // update the selected subscriptions if not present or if has a greater qos
            if (existingSub == null || existingSub.qosLessThan(sub)) {
                subsGroupedByClient[sub.clientId] = sub
            }
        }
        return subsGroupedByClient.values.toList()
    }

    override fun add(newSubscription: Subscription) {
        ctrie.addToTree(newSubscription)
        subscriptionsRepository.addNewSubscription(newSubscription)
    }

    /**
     * Removes subscription from CTrie, adds TNode when the last client unsubscribes, then calls for cleanTomb in a
     * separate atomic CAS operation.
     *
     * @param topic the subscription's topic to remove.
     * @param clientID the Id of client owning the subscription.
     */
    override fun removeSubscription(topic: Topic, clientID: String) {
        ctrie.removeFromTree(topic, clientID)
        subscriptionsRepository.removeSubscription(topic.toString(), clientID)
    }

    override fun size(): Int {
        return ctrie.size()
    }

    override fun dumpTree(): String {
        return ctrie.dumpTree()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CTrieSubscriptionDirectory::class.java)
    }
}
