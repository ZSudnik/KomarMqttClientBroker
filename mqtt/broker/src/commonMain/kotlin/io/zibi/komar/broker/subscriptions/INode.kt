package io.zibi.komar.broker.subscriptions

import java.util.concurrent.atomic.AtomicReference

class INode(mainNode: CNode) {
    private val mainNode = AtomicReference<CNode>()

    init {
        this.mainNode.set(mainNode)
        check(mainNode !is TNode) {  // this should never happen
            "TNode should not be set on mainNnode"
        }
    }

    fun compareAndSet(old: CNode, newNode: CNode): Boolean {
        return mainNode.compareAndSet(old, newNode)
    }

    fun compareAndSet(old: CNode, newNode: TNode): Boolean {
        return mainNode.compareAndSet(old, newNode)
    }

    fun mainNode(): CNode {
        return mainNode.get()
    }

    val isTombed: Boolean
        get() = mainNode() is TNode
}
