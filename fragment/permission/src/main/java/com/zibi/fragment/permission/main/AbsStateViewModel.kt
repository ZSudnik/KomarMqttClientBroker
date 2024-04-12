package com.zibi.fragment.permission.main

import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.freeletics.flowredux.compose.rememberState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
abstract class AbsStateViewModel<State : PermissionState, Action : PermissionAction>(
    private val stateMachine: FlowReduxStateMachine<State, Action>
) : ViewModel() {

    @Composable
    fun remState(): androidx.compose.runtime.State<PermissionState?> = stateMachine.rememberState()

    fun dispatch(action: Action) = viewModelScope.launch {
        stateMachine.dispatch(action = action)
    }
}