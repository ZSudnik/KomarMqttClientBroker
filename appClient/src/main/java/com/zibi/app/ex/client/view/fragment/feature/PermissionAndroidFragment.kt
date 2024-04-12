package com.zibi.app.ex.client.view.fragment.feature

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.zibi.fragment.permission.PermissionNavContent
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.fragments.NavContentContainerFragment
import com.zibi.mod.common.navigation.global.GlobalNavigationManager
import org.koin.android.ext.android.inject


class PermissionAndroidFragment : NavContentContainerFragment()
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

  @Composable
  override fun GetContent(): Unit =
    PermissionNavContent(
      navGraphReady = ::onNavGraphReady,
      navigateToGlobalDestination = { appGlobalNavigationEvent ->
        globalNavigationManager.send( appGlobalNavigationEvent )
      },
    )

  private fun onNavGraphReady(destinationNavigator: DestinationNavigator) {
    deeplink?.let(destinationNavigator.navController::navigate)
  }

  companion object {
    private const val KEY_DEEP_LINK = "deepLinkUri"
    const val TAG_PERMISSION_ANDROID = "PermissionAndroidFragment"

    fun newInstance(deeplink: Uri? = null) =
      PermissionAndroidFragment().apply {
        arguments = Bundle().apply { deeplink?.let { putParcelable(KEY_DEEP_LINK, it) } }
      }
  }

}
