package com.zibi.mod.fragment.start.main

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface StartMainState {
  data object Initialized : StartMainState
}

sealed interface StartMainAction {
    data object GoToFragmentOne : StartMainAction
}

@OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
)
class StartMainStateMachine constructor() :
  FlowReduxStateMachine<StartMainState, StartMainAction>(
    initialState = StartMainState.Initialized
  ),
    StartMainNavigation {

  private val _navEvent: MutableSharedFlow<StartMainNavigation.NavEvent> = MutableSharedFlow()
  override val navEvent: Flow<StartMainNavigation.NavEvent> = _navEvent

  init {
    spec {
      inState<StartMainState.Initialized> {
        onActionEffect { _: StartMainAction.GoToFragmentOne, _ ->
          _navEvent.emit(StartMainNavigation.NavEvent.GoToFragmentSetting)
        }
      }
    }
  }
}