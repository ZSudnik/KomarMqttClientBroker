@file:Suppress("LABEL_NAME_CLASH")

package com.zibi.mod.common.ui.utils

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
const val CLICK_DELAY_MS = 1000
private var lastClickTime: Long = 0

fun ViewModel.launch(delayMs: Int = 0, action: suspend () -> Unit) {
  viewModelScope.launch {
    if (delayMs > 0 && SystemClock.elapsedRealtime() - lastClickTime < delayMs) {
      return@launch
    }
    action()
    lastClickTime = SystemClock.elapsedRealtime()
  }
}