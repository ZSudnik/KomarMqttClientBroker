package com.zibi.mod.common.navigation.global

interface GlobalNavigationManager {

  fun send(event: GlobalNavigationEvent)

  fun register(handler: GlobalNavigationEventHandler): Boolean

  fun unregister(handler: GlobalNavigationEventHandler): Boolean
}

interface GlobalNavigationEventHandler {

  fun handle(event: GlobalNavigationEvent): Boolean
}

interface GlobalNavigationEvent

class GlobalNavigationManagerImpl: GlobalNavigationManager {

  private val lock = Any()

  private val globalNavigationEventHandlers: MutableList<GlobalNavigationEventHandler> = mutableListOf()

  override fun send(event: GlobalNavigationEvent): Unit =
    synchronized(lock) {
      globalNavigationEventHandlers.takeWhile { intentHandler ->
        intentHandler.handle(event).not()
      }
    }

  override fun register(handler: GlobalNavigationEventHandler) =
    synchronized(lock) {
      when {
        globalNavigationEventHandlers.contains(handler) -> false
        else -> {
          globalNavigationEventHandlers.add(
            index = 0,
            element = handler
          )
          true
        }
      }
    }

  override fun unregister(handler: GlobalNavigationEventHandler) =
    synchronized(lock) {
      when {
        globalNavigationEventHandlers.contains(handler) -> {
          globalNavigationEventHandlers.remove(handler)
          true
        }
        else -> false
      }
    }

}
