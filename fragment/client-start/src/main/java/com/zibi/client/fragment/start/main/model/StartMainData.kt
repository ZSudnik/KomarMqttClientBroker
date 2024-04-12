package com.zibi.client.fragment.start.main.model

import android.content.Context
import androidx.compose.runtime.MutableState
import com.zibi.client.fragment.start.data.LightPoint
import com.zibi.common.device.lightbulb.LightBulbData

sealed class StartMainData {
  data class Initialized(
      val stateClient: MutableState<Boolean>,
      val lightPoint: LightPoint,
      val runStopServer: (context: Context) -> Unit,
      val sendDataListOfLightBulb: (listBulb: List<LightBulbData>) -> Unit,
      val changeEndPoint: (lightPoint: LightPoint) -> Unit,
  ) : StartMainData()
}
