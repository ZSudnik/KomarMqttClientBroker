package com.zibi.mod.common.ui.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleEventHandler(onLifecycleEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
  val lifecycleEventHandler = rememberUpdatedState(onLifecycleEvent)
  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

  DisposableEffect(lifecycleOwner.value) {
    val lifecycle = lifecycleOwner.value.lifecycle
    val observer = LifecycleEventObserver(lifecycleEventHandler.value::invoke)

    lifecycle.addObserver(observer)
    onDispose { lifecycle.removeObserver(observer) }
  }
}
