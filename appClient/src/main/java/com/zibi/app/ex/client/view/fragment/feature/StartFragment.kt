package com.zibi.app.ex.client.view.fragment.feature

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.fragments.NavContentContainerFragment
import com.zibi.mod.common.navigation.global.GlobalNavigationManager
import com.zibi.client.fragment.start.StartNavContent
import org.koin.android.ext.android.inject


class StartFragment : NavContentContainerFragment()
{

private val globalNavigationManager : GlobalNavigationManager by inject()

  private var deeplink: Uri? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    globalNavigationManager.register(this)
    deeplink = arguments?.parcelable(KEY_DEEP_LINK)
  }

  override fun onDestroy() {
//    globalNavigationManager.unregister(this)
    super.onDestroy()
  }

//  private var handleGlobalEvents: (globalEvent: AppGlobalNavigationEvent) -> Unit = {}
//
//  override fun handle(event: GlobalNavigationEvent)=
//    when (val appEvent = event as? AppGlobalNavigationEvent) {
//    is AppGlobalNavigationEvent.ToOne -> {
//      handleGlobalEvents(appEvent)
//      true
//    }
//    else -> false // Not handled.
//  }

  @Composable
  override fun GetContent(): Unit =
    StartNavContent(
      navGraphReady = ::onNavGraphReady,
      navigateToGlobalDestination = { appGlobalNavigationEvent ->
        globalNavigationManager.send( appGlobalNavigationEvent )
      },
//      startDestination = StartDestination.Login,
    )

  private fun onNavGraphReady(destinationNavigator: DestinationNavigator) {
    deeplink?.let(destinationNavigator.navController::navigate)
  }

  companion object {
    private const val KEY_DEEP_LINK = "deepLinkUri"
    const val TAG_START = "StartFragment"

    fun newInstance(deeplink: Uri? = null) =
      StartFragment().apply {
        arguments = Bundle().apply { deeplink?.let { putParcelable(KEY_DEEP_LINK, it) } }
      }
  }

}
