package com.zibi.fragment.permission

import com.zibi.mod.common.navigation.Destination

sealed interface PermissionDestination : Destination {
  data object Main : PermissionDestination {
    override val route = "permission/main"
  }
}
