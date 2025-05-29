package com.zibi.mod.fragment.broker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.zibi.mod.common.navigation.AppGlobalNavigationEvent
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.NavEvent
import com.zibi.mod.common.navigation.addDestination
import com.zibi.mod.common.navigation.destinationNavigator
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.Navigation.NavEvent
import com.zibi.mod.fragment.broker.main.SettingMainScreen
import com.zibi.mod.fragment.broker.main.SettingViewModelImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun OneFragmentNavContent(
    navigateToGlobalDestination: (AppGlobalNavigationEvent) -> Unit,
    navGraphReady: (destinationNavigator: DestinationNavigator) -> Unit,
    navResult: () -> Unit,
) {
    val destinationNavigator = destinationNavigator()
    NavHost(
        navController = destinationNavigator.navController,
        startDestination = SettingDestination.Main.route,
    ) {
        addDestination(destination = SettingDestination.Main) {
            val viewModel: SettingViewModelImpl = koinViewModel()

            NavEvent(viewModel.navEvent) { event ->
                when (event) {
                    is NavEvent.Back -> navResult()
                    is NavEvent.FragmentTwo -> navigateToGlobalDestination(AppGlobalNavigationEvent.ToTwo)
                }
            }

            SettingMainScreen(viewModel = viewModel)
        }
    }
    navGraphReady(destinationNavigator)
}
