package com.zibi.mod.fragment.start.main.model

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState

sealed interface StartMainData {
  data class Initialized(
      var stateServer: MutableState<Boolean>,
      val topMenuTitle: String,
      val descState: String,
      val valueStateRun: String,
      val valueStateStop: String,
      val descIPAddress: String,
      val valueIPAddress: String,
      val descNumberClient: String,
      val valueNumberClient: MutableIntState,
      val descLogs: String,
      val runStopServer: (context: Context) -> Unit,
      val onGoToSetting: () -> Unit,
      val onEraserLogList: () -> Unit,
  ) : StartMainData
}
