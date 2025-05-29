package io.zibi.komar.broker.subscriptions

import io.zibi.komar.broker.subscriptions.CTrie.IVisitor
import java.util.concurrent.atomic.AtomicInteger

internal class SubscriptionCounterVisitor() : IVisitor<Int> {
    private val accumulator: AtomicInteger = AtomicInteger(0)
    public override fun visit(node: CNode, deep: Int) {
        accumulator.addAndGet(node.subscriptions.size)
    }

    override val result: Int
        get() {
            return accumulator.get()
        }
}
