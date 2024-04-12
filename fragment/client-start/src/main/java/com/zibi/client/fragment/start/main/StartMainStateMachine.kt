package com.zibi.client.fragment.start.main

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.zibi.client.fragment.start.data.LightPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class StartMainState(open val lightPoint: LightPoint) {
  class LightBulbInit(override val lightPoint: LightPoint) : StartMainState(lightPoint)
}

sealed class StartMainAction {
  data object GoToSetting : StartMainAction()
  data object GoToLivingRoom : StartMainAction()
  data class GoToLightBulb(val lightPoint: LightPoint) : StartMainAction()
}

@OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
)
class StartMainStateMachine:
  FlowReduxStateMachine<StartMainState, StartMainAction>(
    initialState = StartMainState.LightBulbInit(LightPoint.LightOne)
  ),
    StartMainNavigation {

  private val _navEvent: MutableSharedFlow<StartMainNavigation.NavEvent> = MutableSharedFlow()
  override val navEvent: Flow<StartMainNavigation.NavEvent> = _navEvent

  init {
    spec {
      inState<StartMainState.LightBulbInit> {
        onActionEffect { _: StartMainAction.GoToSetting, _ ->
          _navEvent.emit(StartMainNavigation.NavEvent.GoToFragmentSetting)
        }
        on{ action: StartMainAction.GoToLightBulb, state ->
          state.override { StartMainState.LightBulbInit(lightPoint = action.lightPoint) }
        }
      }
    }
  }
}