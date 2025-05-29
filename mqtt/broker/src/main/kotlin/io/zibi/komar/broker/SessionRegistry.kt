package io.zibi.komar.broker

import io.zibi.komar.broker.Session.Will
import io.zibi.komar.broker.Session.SessionStatus.*
import io.zibi.komar.broker.subscriptions.ISubscriptionsDirectory
import io.zibi.komar.broker.subscriptions.Topic
import io.zibi.codec.mqtt.MqttConnectMessage
import io.zibi.codec.mqtt.MqttQoS
import org.slf4j.LoggerFactory
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Function

class SessionRegistry internal constructor(
    private val subscriptionsDirectory: ISubscriptionsDirectory,
    private val queueRepository: IQueueRepository,
    private val authorizator: Authorizator
) {
    abstract class EnqueuedMessage {
        /**
         * Releases any held resources. Must be called when the EnqueuedMessage is no
         * longer needed.
         */
        open fun release() {}

        /**
         * Retains any held resources. Must be called when the EnqueuedMessage is added
         * to a store.
         */
        open fun retain() {}
    }

    class PublishedMessage(val topic: Topic, val publishingQos: MqttQoS, val payload: ByteArray) :
        EnqueuedMessage() {
        override fun release() {
//            payload.release()
        }
        override fun retain() {
//            payload.retain()
        }
    }

    class PubRelMarker : EnqueuedMessage()
    enum class CreationModeEnum {
        CREATED_CLEAN_NEW,
        REOPEN_EXISTING,
        DROP_EXISTING
    }

    class SessionCreationResult(
        val session: Session,
        val mode: CreationModeEnum,
        val alreadyStored: Boolean
    )

    private val pool: ConcurrentMap<String, Session> = ConcurrentHashMap()
    private val queues: ConcurrentMap<String, Queue<EnqueuedMessage>> = ConcurrentHashMap()

    init {
        reloadPersistentQueues()
        recreateSessionPool()
    }

    private fun reloadPersistentQueues() {
        val persistentQueues: Map<String, Queue<EnqueuedMessage>> = queueRepository.listAllQueues()
        persistentQueues.forEach { (k: String, v: Queue<EnqueuedMessage>) -> queues[k] = v }
    }

    private fun recreateSessionPool() {
        for (clientId in subscriptionsDirectory.listAllSessionIds()) {
            // if the subscriptions are present is obviously false
            queues[clientId]?.let {
                pool[clientId] = Session(clientId, false, it)
            }
        }
    }

    fun createOrReopenSession(
        msg: MqttConnectMessage,
        clientId: String,
        username: String
    ): SessionCreationResult {
        val newSession = createNewSession(msg, clientId)
        val oldSession = pool[clientId]
        return if (oldSession == null) {
            // publish the session
            val previous = pool.putIfAbsent(clientId, newSession)
            if (previous != null) {
                reopenExistingSession(msg, clientId, previous, newSession, username)
            } else {
                // case 1
                LOG.trace("case 1, not existing session with CId {}", clientId)
                SessionCreationResult(newSession, CreationModeEnum.CREATED_CLEAN_NEW, false)
            }
        } else {
            reopenExistingSession(msg, clientId, oldSession, newSession, username)
        }
    }

    private fun reopenExistingSession(
        msg: MqttConnectMessage, clientId: String,
        oldSession: Session, newSession: Session, username: String
    ): SessionCreationResult {
        val newIsClean = msg.variableHeader().isCleanSession
        return if (oldSession.disconnected()) {
            if (newIsClean) {
                if( !oldSession.assignState(DISCONNECTED, CONNECTING))
                    throw SessionCorruptedException("old session was already changed state")
                // case 2
                // publish new session
                dropQueuesForClient(clientId)
                unsubscribe(oldSession)
                copySessionConfig(msg, oldSession)
                LOG.trace("case 2, oldSession with same CId {} disconnected", clientId)
                checkSessionOfId(clientId, oldSession)
                SessionCreationResult(oldSession, CreationModeEnum.CREATED_CLEAN_NEW, true)
            } else {
                if( !oldSession.assignState(DISCONNECTED, CONNECTING))
                    throw SessionCorruptedException("old session moved in connected state by other thread")
                // case 3
                reactivateSubscriptions(oldSession, username)
                LOG.trace("case 3, oldSession with same CId {} disconnected", clientId)
                checkSessionOfId(clientId, oldSession)
                SessionCreationResult(oldSession, CreationModeEnum.REOPEN_EXISTING, true)
            }
        } else {
            // case 4
            LOG.trace("case 4, oldSession with same CId {} still connected, force to close", clientId)
            oldSession.closeImmediately()
            LOG.trace("Drop session of already connected client with same id")
            if (!pool.replace(clientId, oldSession, newSession)) {
                //the other client was disconnecting and removed it's own session
                pool[clientId] = newSession
            }
            SessionCreationResult(newSession, CreationModeEnum.DROP_EXISTING, true)
        }
    }

    private fun checkSessionOfId(clientId: String, oldSession: Session){
        LOG.trace("Replace session of client with same id")
        if (!pool.replace(clientId, oldSession, oldSession))
            throw SessionCorruptedException("old session was already removed")
    }

    private fun reactivateSubscriptions(session: Session, username: String) {
        //verify if subscription still satisfy read ACL permissions
        for (existingSub in session.subscriptions) {
            val topicReadable = authorizator.canRead(
                existingSub.topicFilter, username, session.clientID
            )
            if (!topicReadable) {
                subscriptionsDirectory.removeSubscription(existingSub.topicFilter, session.clientID)
            }
            // TODO
//            subscriptionsDirectory.reactivate(existingSub.getTopicFilter(), session.getClientID());
        }
    }

    private fun unsubscribe(session: Session) {
        for (existingSub in session.subscriptions) {
            subscriptionsDirectory.removeSubscription(existingSub.topicFilter, session.clientID)
        }
    }

    private fun createNewSession(msg: MqttConnectMessage, clientId: String): Session {
        val clean = msg.variableHeader().isCleanSession
        val sessionQueue = queues.computeIfAbsent(
            clientId,
            Function<String, Queue<EnqueuedMessage>> { cli: String ->
                queueRepository.createQueue(cli, clean)
            })
        val newSession: Session = if (msg.variableHeader().isWillFlag) {
            Session(clientId, clean, createWill(msg), sessionQueue)
        } else {
            Session(clientId, clean, sessionQueue)
        }
        newSession.markConnecting()
        return newSession
    }

    private fun copySessionConfig(msg: MqttConnectMessage, session: Session) {
        val clean = msg.variableHeader().isCleanSession
        val will: Will? = if (msg.variableHeader().isWillFlag) {
            createWill(msg)
        } else {
            null
        }
        session.update(clean, will)
    }

    private fun createWill(msg: MqttConnectMessage): Will {
//        val willPayload = Unpooled.copiedBuffer(msg.payload().willMessageInBytes())
        val willPayload = msg.payload().willMessageInBytes()
        val willTopic = msg.payload().willTopic()
        val retained = msg.variableHeader().isWillRetain
        val qos = MqttQoS.valueOf(msg.variableHeader().willQos)
        return Will(willTopic!!, willPayload!!, qos, retained)
    }

    fun retrieve(clientID: String): Session? = pool[clientID]

    fun remove(session: Session) {
        pool.remove(session.clientID, session)
    }

    private fun dropQueuesForClient(clientId: String) {
        queues.remove(clientId)
    }

    fun listConnectedClients(): Collection<ClientDescriptor> {
        return pool.values
            .filter { session: Session -> session.connected() }
            .mapNotNull { s: Session ->
                if (s.remoteAddress() != null) {
                    ClientDescriptor(s.clientID, s.remoteAddress()!!)
                } else null
            }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SessionRegistry::class.java)
    }
}
