package com.zibi.app.ex.client.view.fragment.feature

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.zibi.mod.common.navigation.DestinationNavigator
import com.zibi.mod.common.navigation.fragments.NavContentContainerFragment
import com.zibi.mod.common.navigation.global.GlobalNavigationManager
import com.zibi.mod.common.navigation.AppGlobalNavigationEvent
import com.zibi.client.fragment.setting.OneFragmentNavContent
import org.koin.android.ext.android.inject

class SettingFragment : NavContentContainerFragment() {

    private val globalNavigationManager : GlobalNavigationManager by inject()


    private var deeplink: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deeplink = arguments?.parcelable(KEY_DEEP_LINK)
    }

    @Composable
    override fun GetContent(): Unit =
        OneFragmentNavContent(
            navigateToGlobalDestination = { appGlobalNavigationEvent ->
                globalNavigationManager.send(appGlobalNavigationEvent)
            },
            navGraphReady = ::onNavGraphReady,
            navResult = {
                globalNavigationManager.send(AppGlobalNavigationEvent.ToStart)
            },
        )

    private fun onNavGraphReady(destinationNavigator: DestinationNavigator) {
        deeplink?.let(destinationNavigator.navController::navigate)
    }

    companion object {
        private const val KEY_DEEP_LINK = "deepLinkUri"
        const val TAG_FRAGMENT_ONE = "OneFragment"

        fun newInstance(deeplink: Uri? = null) =
            SettingFragment().apply {
                arguments = Bundle().apply { deeplink?.let { putParcelable(KEY_DEEP_LINK, it) } }
            }
    }
}
