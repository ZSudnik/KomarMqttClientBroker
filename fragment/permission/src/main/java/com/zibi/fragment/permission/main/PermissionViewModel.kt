package com.zibi.fragment.permission.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

interface PermissionViewModel {
    fun goToFragment(action: PermissionAction)
    @Composable
    fun rememberState(): State<PermissionState?>
}

interface PermissionNavigation {
    sealed interface NavEvent {
        data object GoToFragmentStart : NavEvent
    }
    val navEvent: Flow<NavEvent>
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PermissionViewModelImpl (
    private val stateMachine: PermissionStateMachine,
) : AbsStateViewModel<PermissionState, PermissionAction>(stateMachine),
    PermissionViewModel, PermissionNavigation {

    @Composable
    override fun rememberState(): State<PermissionState?> = super.remState()
    override fun goToFragment(action: PermissionAction){
        super.dispatch(action = action )
    }
    override val navEvent: Flow<PermissionNavigation.NavEvent>
        get() = stateMachine.navEvent

}

