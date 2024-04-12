package com.zibi.mod.common.navigation.intents

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.zibi.mod.common.lifecycle.ActivityLifecycleConnector

interface IntentManagerConnector : ActivityLifecycleConnector

interface IntentManager {

  fun onNewIntent(intent: Intent)

  fun register(handler: IntentHandler): Boolean

  fun unregister(handler: IntentHandler): Boolean
}

interface IntentHandler {
  suspend fun handle(intent: Intent): Boolean
}

class IntentManagerImpl constructor() :
  IntentManager,
  IntentManagerConnector,
  DefaultLifecycleObserver {

  private val lock = Any()

  private val intentHandlers: MutableList<IntentHandler> = mutableListOf()

  private var lifecycleOwner: LifecycleOwner? = null

  override fun connect(activity: ComponentActivity) = synchronized(lock) {
    intentHandlers.clear()
    this.lifecycleOwner?.lifecycle?.removeObserver(this)

    this.lifecycleOwner = activity
    activity.lifecycle.addObserver(this)
  }

  override fun onDestroy(owner: LifecycleOwner): Unit =
    synchronized(lock) {
      intentHandlers.clear()
    }

  override fun onNewIntent(intent: Intent): Unit =
    synchronized(lock) {
      val lifecycleScope = lifecycleOwner?.lifecycleScope ?: return

      lifecycleScope.launch {
        intentHandlers.takeWhile { intentHandler ->
          intentHandler.handle(intent).not()
        }
      }
    }

  override fun register(handler: IntentHandler) =
    synchronized(lock) {
      when {
        intentHandlers.contains(handler) -> false
        else -> {
          intentHandlers.add(handler)
          true
        }
      }
    }

  override fun unregister(handler: IntentHandler) =
    synchronized(lock) {
      when {
        intentHandlers.contains(handler) -> {
          intentHandlers.remove(handler)
          true
        }
        else -> false
      }
    }

}

