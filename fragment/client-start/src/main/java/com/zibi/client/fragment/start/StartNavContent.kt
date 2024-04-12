package com.zibi.client.fragment.start

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.zibi.mod.common.navigation.AppGlobalNavigationEvent
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.NavEvent
import com.zibi.mod.common.navigation.addDestination
import com.zibi.mod.common.navigation.destinationNavigator
import com.zibi.client.fragment.start.login.StartLoginNavigation
import com.zibi.client.fragment.start.login.StartLoginScreen
import com.zibi.client.fragment.start.login.StartLoginViewModelImpl
import com.zibi.client.fragment.start.main.StartMainNavigation
import com.zibi.client.fragment.start.main.StartMainScreen
import com.zibi.client.fragment.start.main.StartMainViewModelImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartNavContent(
  navGraphReady: (destinationNavigator: DestinationNavigator) -> Unit,
  navigateToGlobalDestination: (AppGlobalNavigationEvent) -> Unit,
//  startDestination: StartDestination? = null,
) {
  val destinationNavigator = destinationNavigator()
  NavHost(
    navController = destinationNavigator.navController,
    startDestination = StartDestination.Login.route,
  ) {
    addDestination(destination = StartDestination.Login) {
      val viewModel: StartLoginViewModelImpl = koinViewModel()

      NavEvent(viewModel.navEvent) { event ->
        when (event) {
          is StartLoginNavigation.NavEvent.GoToStartMainScreen -> {
            destinationNavigator.toDestination(destination = StartDestination.Main)
          }
        }
      }

      StartLoginScreen(viewModel = viewModel)
    }
    addDestination(destination = StartDestination.Main) {
      val viewModel: StartMainViewModelImpl = koinViewModel()

      NavEvent(viewModel.navEvent) { event ->
        when (event) {
          is StartMainNavigation.NavEvent.GoToFragmentMonitor -> navigateToGlobalDestination(AppGlobalNavigationEvent.ToSetting)
          is StartMainNavigation.NavEvent.GoToFragmentSetting -> navigateToGlobalDestination(AppGlobalNavigationEvent.ToSetting)
        }
      }

      StartMainScreen(viewModel = viewModel)
    }
  }
  navGraphReady(destinationNavigator)
}

