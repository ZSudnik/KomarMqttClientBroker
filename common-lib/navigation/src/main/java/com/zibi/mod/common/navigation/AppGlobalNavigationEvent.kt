package com.zibi.mod.common.navigation

import com.zibi.mod.common.navigation.global.GlobalNavigationEvent

sealed class AppGlobalNavigationEvent : GlobalNavigationEvent {
  data object ToCheckPermission : AppGlobalNavigationEvent()
  data object ToStart : AppGlobalNavigationEvent() //ToLightBulb
  data object ToSetting : AppGlobalNavigationEvent()
  data object ToTwo: AppGlobalNavigationEvent()
}