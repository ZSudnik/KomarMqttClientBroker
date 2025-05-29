package io.zibi.komar.persistence

import io.zibi.komar.broker.SessionRegistry.EnqueuedMessage
import org.h2.mvstore.MVMap
import org.h2.mvstore.MVStore
import java.util.AbstractQueue
import java.util.concurrent.atomic.AtomicLong

internal class H2PersistentQueue(store: MVStore, queueName: String?) :
    AbstractQueue<EnqueuedMessage>() {
    private val queueMap: MVMap<Long, EnqueuedMessage>
    private val metadataMap: MVMap<String, Long>
    private val head: AtomicLong
    private val tail: AtomicLong

    init {
        require(!queueName.isNullOrEmpty()) { "queueName parameter can't be empty or null" }
        val messageTypeBuilder = MVMap.Builder<Long, EnqueuedMessage>()
            .valueType(EnqueuedMessageValueType())
        queueMap = store.openMap(
            "queue_$queueName", messageTypeBuilder
        )
        metadataMap = store.openMap("queue_" + queueName + "_meta")

        //setup head index
        var headIdx = 0L
        if (metadataMap.containsKey("head")) {
            headIdx = metadataMap["head"]!!
        } else {
            metadataMap["head"] = headIdx
        }
        head = AtomicLong(headIdx)

        //setup tail index
        var tailIdx = 0L
        if (metadataMap.containsKey("tail")) {
            tailIdx = metadataMap["tail"]!!
        } else {
            metadataMap["tail"] = tailIdx
        }
        tail = AtomicLong(tailIdx)
    }

    override fun iterator(): MutableIterator<EnqueuedMessage?> {
        return object: MutableIterator<EnqueuedMessage?>{
            override fun hasNext(): Boolean = false
            override fun next(): EnqueuedMessage? = null
            override fun remove() {}
        }
    }

    override val size: Int = head.toInt() - tail.toInt()

    override fun offer(t: EnqueuedMessage?): Boolean {
        if (t == null) {
            throw NullPointerException("Inserted element can't be null")
        }
        val nextHead = head.getAndIncrement()
        queueMap[nextHead] = t
        metadataMap["head"] = nextHead + 1
        return true
    }

    override fun poll(): EnqueuedMessage? {
        if (head == tail) {
            return null
        }
        val nextTail = tail.getAndIncrement()
        val tail = queueMap[nextTail]
        queueMap.remove(nextTail)
        metadataMap["tail"] = nextTail + 1
        return tail
    }

    override fun peek(): EnqueuedMessage? {
        return if (head == tail) {
            null
        } else queueMap[tail.get()]
    }

    companion object {
        fun dropQueue(store: MVStore, queueName: String) {
            store.removeMap(store.openMap<Any, Any>("queue_$queueName"))
            store.removeMap(store.openMap<Any, Any>("queue_" + queueName + "_meta"))
        }
    }
}
