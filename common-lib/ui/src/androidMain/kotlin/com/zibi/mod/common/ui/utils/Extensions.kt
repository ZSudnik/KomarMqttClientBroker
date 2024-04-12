package com.zibi.mod.common.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*

fun Context.showToast(message: String) {
  Toast.makeText(
    this,
    message,
    Toast.LENGTH_SHORT
  ).show()
}

@OptIn(ExperimentalMaterialApi::class)
suspend fun BottomSheetScaffoldState.toggleBottomSheet() {
  when (this.bottomSheetState.isCollapsed) {
    true -> this.bottomSheetState.expand()
    else -> this.bottomSheetState.collapse()
  }
}

@OptIn(ExperimentalMaterialApi::class)
suspend fun ModalBottomSheetState.toggle() {
  if (isVisible) hide() else show()
}

fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

fun CoroutineScope.launchPeriodicAsync(
  repeatMillis: Long,
  action: () -> Unit
) = this.async {
  if (repeatMillis > 0) {
    while (isActive) {
      action()
      delay(repeatMillis)
    }
  } else {
    action()
  }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
  val eventHandler = rememberUpdatedState(onEvent)
  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

  DisposableEffect(lifecycleOwner.value) {
    val lifecycle = lifecycleOwner.value.lifecycle
    val observer = LifecycleEventObserver { owner, event ->
      eventHandler.value(owner, event)
    }

    lifecycle.addObserver(observer)
    onDispose {
      lifecycle.removeObserver(observer)
    }
  }
}