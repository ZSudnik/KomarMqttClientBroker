package io.zibi.komar.broker

import io.zibi.komar.broker.SessionRegistry.EnqueuedMessage
import java.util.Collections
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class MemoryQueueRepository : IQueueRepository {
    private val queues: MutableMap<String, Queue<EnqueuedMessage>> = mutableMapOf()

    override fun createQueue(cli: String, clean: Boolean): Queue<EnqueuedMessage> {
        val queue = ConcurrentLinkedQueue<EnqueuedMessage>()
        queues[cli] = queue
        return queue
    }

    override fun listAllQueues(): Map<String, Queue<EnqueuedMessage>> {
        return Collections.unmodifiableMap(queues)
    }
}
