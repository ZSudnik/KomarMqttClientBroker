package com.zibi.mod.fragment.start.main

import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.freeletics.flowredux.compose.rememberState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
abstract class AbsStateViewModel<State : StartMainState, Action : StartMainAction>(
    private val stateMachine: FlowReduxStateMachine<State, Action>
) : ViewModel() {

    @Composable
    fun remState(): androidx.compose.runtime.State<StartMainState?> = stateMachine.rememberState()

    fun dispatch(action: Action) = viewModelScope.launch {
        stateMachine.dispatch(action = action)
    }
}