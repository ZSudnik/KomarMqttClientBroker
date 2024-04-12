package io.zibi.komar.broker

import io.zibi.komar.broker.SessionRegistry.EnqueuedMessage
import java.util.Queue

interface IQueueRepository {
    fun createQueue(cli: String, clean: Boolean): Queue<EnqueuedMessage>
    fun listAllQueues(): Map<String, Queue<EnqueuedMessage>>
}
