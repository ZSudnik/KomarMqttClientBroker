package io.zibi.komar.persistence

import io.zibi.komar.broker.IQueueRepository
import io.zibi.komar.broker.SessionRegistry.EnqueuedMessage
import org.h2.mvstore.MVStore
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class H2QueueRepository(private val mvStore: MVStore) : IQueueRepository {
    override fun createQueue(cli: String, clean: Boolean): Queue<EnqueuedMessage> {
        return if (!clean) {
            H2PersistentQueue(mvStore, cli)
        } else ConcurrentLinkedQueue()
    }

    override fun listAllQueues(): Map<String, Queue<EnqueuedMessage>> {
        val result: MutableMap<String, Queue<EnqueuedMessage>> = HashMap()
        mvStore.mapNames.stream()
            .filter { name: String -> name.startsWith("queue_") && !name.endsWith("_meta") }
            .map { name: String -> name.substring("queue_".length) }
            .forEach { name: String ->
                result[name] = H2PersistentQueue(mvStore, name)  as Queue<EnqueuedMessage>
            }
        return result
    }
}
