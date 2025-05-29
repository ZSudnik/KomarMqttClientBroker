package io.zibi.komar.broker.subscriptions

import java.util.Optional

class CTrie internal constructor() {
    internal interface IVisitor<T> {
        fun visit(node: CNode, deep: Int)
        val result: T
    }

    private enum class Action {
        OK,
        REPEAT
    }

    var root: INode

    init {
        val mainNode = CNode()
        mainNode.token = ROOT
        this.root = INode(mainNode)
    }

    fun lookup(topic: Topic): Optional<CNode> {
        var topic1 = topic
        var inode: INode = this.root
        var token = topic1.headToken()
        while (!topic1.isEmpty && inode.mainNode().anyChildrenMatch(token)) {
            topic1 = topic1.exceptHeadToken()
            inode = inode.mainNode().childOf(token)
            token = topic1.headToken()
        }
        return if (!topic1.isEmpty) {
            Optional.empty<CNode>()
            //Optional.empty()
        } else Optional.of<CNode>(inode.mainNode())
    }

    internal enum class NavigationAction {
        MATCH,
        GODEEP,
        STOP
    }

    private fun evaluate(topic: Topic, cnode: CNode?): NavigationAction {
        if (Token.MULTI == cnode?.token) {
            return NavigationAction.MATCH
        }
        if (topic.isEmpty) {
            return NavigationAction.STOP
        }
        val token = topic.headToken()
        return if (!(Token.SINGLE == cnode?.token || cnode?.token == token || ROOT == cnode?.token)) {
            NavigationAction.STOP
        } else NavigationAction.GODEEP
    }

    fun recursiveMatch(topic: Topic): List<Subscription> {
        return recursiveMatch(topic, this.root)
    }

    private fun recursiveMatch(topic: Topic, inode: INode): List<Subscription> {
        val cnode = inode.mainNode()
        if (cnode is TNode) {
            return emptyList()
        }
        val action = evaluate(topic, cnode)
        if (action == NavigationAction.MATCH) {
            return cnode.subscriptions
        }
        if (action == NavigationAction.STOP) {
            return emptyList()
        }
        val remainingTopic = if (ROOT == cnode.token) topic else topic.exceptHeadToken()
        val subscriptions: MutableList<Subscription> = mutableListOf()
        if (remainingTopic.isEmpty) {
            subscriptions.addAll(cnode.subscriptions)
        }
        for (subInode in cnode.allChildren()) {
            subscriptions.addAll(recursiveMatch(remainingTopic, subInode))
        }
        return subscriptions
    }

    fun addToTree(newSubscription: Subscription) {
        var res: Action
        do {
            res = insert(newSubscription.topicFilter, this.root, newSubscription)
        } while (res == Action.REPEAT)
    }

    private fun insert(topic: Topic, inode: INode, newSubscription: Subscription): Action {
        val token = topic.headToken()
        return if (!topic.isEmpty && inode.mainNode().anyChildrenMatch(token)) {
            val remainingTopic = topic.exceptHeadToken()
            val nextInode = inode.mainNode().childOf(token)
            insert(remainingTopic, nextInode, newSubscription)
        } else {
            if (topic.isEmpty) {
                insertSubscription(inode, newSubscription)
            } else {
                createNodeAndInsertSubscription(topic, inode, newSubscription)
            }
        }
    }

    private fun insertSubscription(inode: INode, newSubscription: Subscription): Action {
        val cnode = inode.mainNode()
        val updatedCnode = cnode.copy().addSubscription(newSubscription)
        return if (inode.compareAndSet(cnode, updatedCnode)) {
            Action.OK
        } else {
            Action.REPEAT
        }
    }

    private fun createNodeAndInsertSubscription(
        topic: Topic,
        inode: INode,
        newSubscription: Subscription
    ): Action {
        val newInode = createPathRec(topic, newSubscription)
        val cnode = inode.mainNode()
        val updatedCnode = cnode.copy()
        updatedCnode.add(newInode)
        return if (inode.compareAndSet(cnode, updatedCnode)) Action.OK else Action.REPEAT
    }

    private fun createPathRec(topic: Topic, newSubscription: Subscription): INode {
        val remainingTopic = topic.exceptHeadToken()
        return if (!remainingTopic.isEmpty) {
            val inode = createPathRec(remainingTopic, newSubscription)
            val cnode = CNode()
            cnode.token = topic.headToken()
            cnode.add(inode)
            INode(cnode)
        } else {
            createLeafNodes(topic.headToken(), newSubscription)
        }
    }

    private fun createLeafNodes(token: Token?, newSubscription: Subscription): INode {
        val newLeafCnode = CNode()
        newLeafCnode.token = token
        newLeafCnode.addSubscription(newSubscription)
        return INode(newLeafCnode)
    }

    fun removeFromTree(topic: Topic, clientID: String) {
        var res: Action
        do {
            res = remove(clientID, topic, this.root, NO_PARENT)
        } while (res == Action.REPEAT)
    }

    private fun remove(clientId: String, topic: Topic, inode: INode, iParent: INode): Action {
        val token = topic.headToken()
        return if (!topic.isEmpty && inode.mainNode().anyChildrenMatch(token)) {
            val remainingTopic = topic.exceptHeadToken()
            val nextInode = inode.mainNode().childOf(token)
            remove(clientId, remainingTopic, nextInode, inode)
        } else {
            val cnode = inode.mainNode()
            if (cnode is TNode) {
                // this inode is a tomb, has no clients and should be cleaned up
                // Because we implemented cleanTomb below, this should be rare, but possible
                // Consider calling cleanTomb here too
                return Action.OK
            }
            if (cnode.containsOnly(clientId) && topic.isEmpty && cnode.allChildren().isEmpty()) {
                // last client to leave this node, AND there are no downstream children, remove via TNode tomb
                if (inode === this.root) {
                    return if (inode.compareAndSet(
                            cnode,
                            inode.mainNode().copy()
                        )
                    ) Action.OK else Action.REPEAT
                }
                val tnode = TNode()
                if (inode.compareAndSet(cnode, tnode)) cleanTomb(
                    inode,
                    iParent
                ) else Action.REPEAT
            } else if (cnode.contains(clientId) && topic.isEmpty) {
                val updatedCnode = cnode.copy()
                updatedCnode.removeSubscriptionsFor(clientId)
                if (inode.compareAndSet(
                        cnode,
                        updatedCnode
                    )
                ) Action.OK else Action.REPEAT
            } else {
                //someone else already removed
                Action.OK
            }
        }
    }

    /**
     *
     * Cleans Disposes of TNode in separate Atomic CAS operation per
     * http://bravenewgeek.com/breaking-and-entering-lose-the-lock-while-embracing-concurrency/
     *
     * We roughly follow this theory above, but we allow CNode with no Subscriptions to linger (for now).
     *
     *
     * @param inode inode that handle to the tomb node.
     * @param iParent inode parent.
     * @return REPEAT if the this methods wasn't successful or OK.
     */
    private fun cleanTomb(inode: INode?, iParent: INode): Action {
        val updatedCnode = iParent.mainNode().copy()
        updatedCnode.remove(inode)
        return if (iParent.compareAndSet(
                iParent.mainNode(),
                updatedCnode
            )
        ) Action.OK else Action.REPEAT
    }

    fun size(): Int {
        val visitor = SubscriptionCounterVisitor()
        dfsVisit(this.root, visitor, 0)
        return visitor.result
    }

    fun dumpTree(): String {
        val visitor = DumpTreeVisitor()
        dfsVisit(this.root, visitor, 0)
        return visitor.result
    }

    private fun dfsVisit(node: INode?, visitor: IVisitor<*>, deep: Int) {
        var m_deep = deep
        if (node == null) {
            return
        }
        visitor.visit(node.mainNode(), m_deep)
        ++m_deep
        for (child in node.mainNode().allChildren()) {
            dfsVisit(child, visitor, m_deep)
        }
    }

    companion object {
        private val ROOT = Token("root")
        private val NO_PARENT: INode = INode(CNode())
    }
}
