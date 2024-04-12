package io.zibi.komar.broker.subscriptions

class TNode() : CNode() {

    override var token: Token?
        get() = throw IllegalStateException("Can't be invoked on TNode")
        set(value) = throw IllegalStateException("Can't be invoked on TNode set $value")

    override fun childOf(token: Token?): INode =throw IllegalStateException("Can't be invoked on TNode")

    override fun copy(): CNode = throw IllegalStateException("Can't be invoked on TNode")

    override fun add(newINode: INode) = throw IllegalStateException("Can't be invoked on TNode")

    override fun addSubscription(newSubscription: Subscription): CNode = throw IllegalStateException("Can't be invoked on TNode")

    override fun containsOnly(clientId: String): Boolean = throw IllegalStateException("Can't be invoked on TNode")

    override fun contains(clientId: String): Boolean = throw IllegalStateException("Can't be invoked on TNode")

    override fun removeSubscriptionsFor(clientId: String) = throw IllegalStateException("Can't be invoked on TNode")

    override fun anyChildrenMatch(token: Token?): Boolean {
        return false
    }
}
