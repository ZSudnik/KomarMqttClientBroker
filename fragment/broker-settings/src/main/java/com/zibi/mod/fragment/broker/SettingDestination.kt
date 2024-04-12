package com.zibi.mod.fragment.broker

import com.zibi.mod.common.navigation.Destination

sealed interface SettingDestination : Destination {
  object Main : SettingDestination {
    override val route = "one_fragment/main"
  }
}
