package com.zibi.mod.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <EVENT> NavEvent(
  navEvent: Flow<EVENT>,
  navEventHandler: (EVENT) -> Unit
) {
  LaunchedEffect(navEvent) {
    navEvent.collect { result ->
      navEventHandler(result)
    }
  }
}
