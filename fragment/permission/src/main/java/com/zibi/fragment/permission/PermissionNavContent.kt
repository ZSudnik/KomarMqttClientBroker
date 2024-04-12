package com.zibi.fragment.permission

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.zibi.mod.common.navigation.AppGlobalNavigationEvent
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.NavEvent
import com.zibi.mod.common.navigation.addDestination
import com.zibi.mod.common.navigation.destinationNavigator
import com.zibi.fragment.permission.main.PermissionNavigation
import com.zibi.fragment.permission.main.PermissionScreen
import com.zibi.fragment.permission.main.PermissionViewModelImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionNavContent(
  navGraphReady: (destinationNavigator: DestinationNavigator) -> Unit,
  navigateToGlobalDestination: (AppGlobalNavigationEvent) -> Unit,
//  startDestination: StartDestination? = null,
) {
  val destinationNavigator = destinationNavigator()
  NavHost(
    navController = destinationNavigator.navController,
    startDestination = PermissionDestination.Main.route,
  ) {
    addDestination(destination = PermissionDestination.Main) {
      val viewModel: PermissionViewModelImpl = koinViewModel()

      NavEvent(viewModel.navEvent) { event ->
        when (event) {
          is PermissionNavigation.NavEvent.GoToFragmentStart -> navigateToGlobalDestination(AppGlobalNavigationEvent.ToStart)
        }
      }

      PermissionScreen(viewModel = viewModel)
    }
  }
  navGraphReady(destinationNavigator)
}

