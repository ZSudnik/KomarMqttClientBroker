package com.zibi.mod.common.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel

/**
 * Screen destination inside the feature.
 */
interface Destination {

  val route: String

  val arguments: List<NamedNavArgument> get() = emptyList()

  val deepLinks: List<NavDeepLink> get() = emptyList()
}

fun NavGraphBuilder.addDestination(
  destination: Destination,
  content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)
) {
  composable(
    route = destination.route,
    arguments = destination.arguments,
    deepLinks = destination.deepLinks,
    content = content,
  )
}

fun NavGraphBuilder.addDialogDestination(
  destination: Destination,
  dialogProperties: DialogProperties,
  content: @Composable (NavBackStackEntry) -> Unit,
) {
  dialog(
    route = destination.route,
    arguments = destination.arguments,
    deepLinks = destination.deepLinks,
    dialogProperties = dialogProperties,
    content = content,
  )
}

@Composable
fun destinationNavigator(): DestinationNavigator {
  val navHostController = rememberNavController()
//  val navigationViewModel = NavigationViewModelImpl()
  val navigationViewModel: NavigationViewModelImpl = koinViewModel()
//  val navigationViewModel = hiltViewModel<NavigationViewModelImpl>()

  return DestinationNavigator(
    navController = navHostController,
    navigationViewModel = navigationViewModel,
  )
}
