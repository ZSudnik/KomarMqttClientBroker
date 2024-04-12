package com.zibi.mod.fragment.start

import com.zibi.mod.common.navigation.Destination

sealed interface StartDestination : Destination {
  data object Login : StartDestination {
    override val route = "start/login"
  }
  data object Main : StartDestination {
    override val route = "start/main"
  }
}
