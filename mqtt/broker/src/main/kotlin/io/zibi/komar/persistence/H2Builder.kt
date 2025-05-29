package io.zibi.komar.persistence

import io.zibi.komar.broker.IQueueRepository
import io.zibi.komar.broker.IRetainedRepository
import io.zibi.komar.broker.ISubscriptionsRepository
import io.zibi.komar.broker.config.IConfig
import io.zibi.komar.BrokerConstants
import org.h2.mvstore.MVStore
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class H2Builder(props: IConfig, scheduler: ScheduledExecutorService) {
    private val storePath: String
    private val autosaveInterval : Int// in seconds
    private val scheduler: ScheduledExecutorService
    private var mvStore: MVStore

    init {
        storePath = props.getProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME, "")
        autosaveInterval = props.intProp(BrokerConstants.AUTOSAVE_INTERVAL_PROPERTY_NAME, 30)
        this.scheduler = scheduler
        LOG.info("Initializing H2 store")
        require(storePath.isNotEmpty()) { "H2 store path can't be null or empty" }
        mvStore = MVStore.Builder()
            .fileName(storePath)
            .autoCommitDisabled()
            .open()
        LOG.trace("Scheduling H2 commit task")
        scheduler.scheduleWithFixedDelay({
            LOG.trace("Committing to H2")
            mvStore.commit()
        }, autosaveInterval.toLong(), autosaveInterval.toLong(), TimeUnit.SECONDS)
    }

    fun subscriptionsRepository(): ISubscriptionsRepository {
        return H2SubscriptionsRepository(mvStore)
    }

    fun closeStore() {
        mvStore.close()
    }

    fun queueRepository(): IQueueRepository {
        return H2QueueRepository(mvStore)
    }

    fun retainedRepository(): IRetainedRepository {
        return H2RetainedRepository(mvStore)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(H2Builder::class.java)
    }
}
