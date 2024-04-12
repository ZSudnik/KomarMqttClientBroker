package com.zibi.client.fragment.start.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import com.zibi.client.fragment.start.login.model.StartLoginData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

interface StartLoginViewModel {
    val uiData: () -> StartLoginData.Screen
    @Composable
    fun rememberState(): State<StartLoginState?>
}

interface StartLoginNavigation {
    sealed interface NavEvent {
        data object GoToStartMainScreen : NavEvent
    }
    val navEvent: Flow<NavEvent>
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class StartLoginViewModelImpl constructor(
    private val stateMachine: StartLoginStateMachine,
//    private val stringResolver: StringResolver,
) : AbsStateLoginViewModel<StartLoginState, StartLoginAction>(stateMachine),
    StartLoginViewModel, StartLoginNavigation {

    @Composable
    override fun rememberState(): State<StartLoginState?> =
        super.remState()

    override val uiData: () -> StartLoginData.Screen
        get() = {
            StartLoginData.Screen(
                topMenuTitle = "",//stringResolver.getString(R.string.fragment_start_login_top_bar_title),
                labelUser = "",//stringResolver.getString(R.string.fragment_start_login_label_username),
                hintUser = "",//stringResolver.getString(R.string.fragment_start_login_hint_username),
                labelPassword = "",//stringResolver.getString(R.string.fragment_start_login_label_password),
                hintPassword = "",//stringResolver.getString(R.string.fragment_start_login_hint_password),
                bottomButtonText = "",//stringResolver.getString(R.string.fragment_start_login_button),
                onBottomNavigationButton = { entryUser, entryPassword ->
                    super.dispatch(action = StartLoginAction.TryLogin(
                        enterUserName = entryUser,
                        enterPassword = entryPassword
                    )
                    )
                },
                dialogTitle = "",//stringResolver.getString(R.string.fragment_start_login_dialog_title),
                dialogContent = "",//stringResolver.getString(R.string.fragment_start_login_dialog_context),
                dialogButtonLabel = "",//stringResolver.getString(R.string.fragment_start_login_dialog_first_button),
                onDialogButtonClicked =  { super.dispatch(action = StartLoginAction.OKDialogBadLogin)},
                )
        }

    override val navEvent: Flow<StartLoginNavigation.NavEvent>
        get() = stateMachine.navEvent

}

