package com.zibi.mod.common.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions

class DestinationNavigator(
  val navController: NavHostController,
  private val navigationViewModel: NavigationViewModel,
) {

  fun toDestination(
    destination: Destination
  ) {
    toDestination(
      destination = destination,
      data = null,
    )
  }

  fun <T : Any> toDestination(
    destination: Destination,
    data: T? = null,
  ) {
    data?.let { nonNullData ->
      navigationViewModel.store(destination, nonNullData)
    }

    val result = runCatching {
      navController.getBackStackEntry(destination.route)
    }
    when {
      result.isSuccess -> {
        navController.popBackStack(
          route = destination.route,
          inclusive = false,
        )
      }
      result.isFailure -> {
        navController.navigate(
          destination.route,
          navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .build()
        )
      }
    }
  }

  fun navigateWithPopUpToId(destination: Destination) {
    navController.navigate(route = destination.route) {
      popUpToId
    }
  }

  fun <T : Any> retrieveData(
    destination: Destination
  ): T? {
    return navigationViewModel.retrieve<T>(destination)
  }

  fun <T : Any> retrieveData(
    destination: Destination,
    onRetrieved: (T) -> Unit
  ) {
    navigationViewModel.retrieve<T>(destination)?.let { data ->
      onRetrieved(data)
    }
  }

  fun pop(): Boolean {
    return navController.popBackStack()
  }
}
