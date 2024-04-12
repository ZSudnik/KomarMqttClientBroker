package com.zibi.mod.common.lifecycle

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class ActivityForResultManager<INTENT, RESULT> :
  ActivityLifecycleConnector,
  LifecycleEventObserver {

  abstract val contract: ActivityResultContract<INTENT, RESULT>

  protected var componentActivity: ComponentActivity? = null

  private var activityResultLauncher: ActivityResultLauncher<INTENT>? = null
  private var resultEmitter: MutableSharedFlow<RESULT> = MutableSharedFlow()

  override fun connect(activity: ComponentActivity) {
    clear()

    componentActivity = activity
    activityResultLauncher = componentActivity?.registerForActivityResult(
      contract
    ) { result ->
      activity.lifecycleScope.launch {
        resultEmitter.emit(result)
      }
    }
    activity.lifecycle.addObserver(this)
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    when (event) {
      Lifecycle.Event.ON_DESTROY -> clear()
      else -> Unit
    }
  }

  protected suspend fun launchForResult(intent: INTENT): RESULT {
    activityResultLauncher?.launch(intent)
    return resultEmitter.first()
  }

  private fun clear() {
    componentActivity?.lifecycle?.removeObserver(this)
    componentActivity = null
    activityResultLauncher = null
  }
}
