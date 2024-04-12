package io.zibi.komar.broker.subscriptions

import java.util.Objects

open class CNode(
    private var children: MutableList<INode> = mutableListOf(),
    var subscriptions: MutableList<Subscription> = mutableListOf()
) {
    open var token: Token? = null

    //Copy constructor
    private constructor(token: Token?, children: List<INode>, subscriptions: List<Subscription>):
    this(
        children = children.toMutableList(),
        subscriptions = subscriptions.toMutableList(),
    ){
        this.token = token // keep reference, root comparison in directory logic relies on it for now.
    }

    open fun anyChildrenMatch(token: Token?): Boolean {
        for (iNode in children) {
            val child = iNode.mainNode()
            if (child.equalsToken(token)) {
                return true
            }
        }
        return false
    }

    fun allChildren(): List<INode> {
        return children
    }

    open fun childOf(token: Token?): INode {
        for (iNode in children) {
            val child = iNode.mainNode()
            if (child.equalsToken(token)) {
                return iNode
            }
        }
        throw IllegalArgumentException("Asked for a token that doesn't exists in any child [$token]")
    }

    private fun equalsToken(token: Token?): Boolean {
        return token != null && this.token != null && this.token == token
    }

    override fun hashCode(): Int {
        return Objects.hash(token)
    }

    open fun copy(): CNode {
        return CNode(token, children, subscriptions)
    }

    open fun add(newINode: INode) {
        children.add(newINode)
    }

    fun remove(node: INode?) {
        children.remove(node)
    }

    open fun addSubscription(newSubscription: Subscription): CNode {
        // if already contains one with same topic and same client, keep that with higher QoS
        if (subscriptions.contains(newSubscription)) {
            val existing = subscriptions.first { s: Subscription -> s == newSubscription }
            if (existing.requestedQos.value() < newSubscription.requestedQos.value()) {
                subscriptions.remove(existing)
                subscriptions.add(Subscription(newSubscription))
            }
        } else {
            subscriptions.add(Subscription(newSubscription))
        }
        return this
    }

    /**
     * @return true iff the subscriptions contained in this node are owned by clientId
     * AND at least one subscription is actually present for that clientId
     */
    open fun containsOnly(clientId: String): Boolean {
        for (sub in subscriptions) {
            if (sub.clientId != clientId) {
                return false
            }
        }
        return !subscriptions.isEmpty()
    }

    //TODO this is equivalent to negate(containsOnly(clientId))
    open operator fun contains(clientId: String): Boolean {
        for (sub in subscriptions) {
            if (sub.clientId == clientId) {
                return true
            }
        }
        return false
    }

    open fun removeSubscriptionsFor(clientId: String) {
        val toRemove: MutableSet<Subscription> = HashSet()
        for (sub in subscriptions) {
            if (sub.clientId == clientId) {
                toRemove.add(sub)
            }
        }
        subscriptions.removeAll(toRemove)
    }
}
