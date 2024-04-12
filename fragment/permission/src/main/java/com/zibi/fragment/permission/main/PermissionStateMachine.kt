package com.zibi.fragment.permission.main

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.zibi.fragment.permission.R
import com.zibi.fragment.permission.model.PermissionData
import com.zibi.mod.common.resources.StringResolver
import com.zibi.mod.common.resources.StringResolverImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface PermissionState {
  data object Init : PermissionState
  class ContentState(val data: PermissionData) : PermissionState
}

sealed class PermissionAction {
  data object GoToStart : PermissionAction()
}

@OptIn(
  FlowPreview::class,
  ExperimentalCoroutinesApi::class,
)
class PermissionStateMachine(
  private val stringResolver: StringResolver,
):
  FlowReduxStateMachine<PermissionState, PermissionAction>(
    initialState = PermissionState.Init
  ), PermissionNavigation {

  private val _navEvent: MutableSharedFlow<PermissionNavigation.NavEvent> = MutableSharedFlow()
  override val navEvent: Flow<PermissionNavigation.NavEvent> = _navEvent

  init {
    spec {
      inState<PermissionState.Init> {
        onEnter { state ->
          state.override { PermissionState.ContentState(
            PermissionData(
              textTitle = stringResolver.getString(R.string.fragment_permission_top_bar_title),
              textAsk = stringResolver.getString(R.string.fragment_permission_text),
              textButton = stringResolver.getString(R.string.fragment_permission_button),
            )
          ) }
        }
      }
      inState<PermissionState.ContentState> {
        onActionEffect { _: PermissionAction.GoToStart, _ ->
          _navEvent.emit(PermissionNavigation.NavEvent.GoToFragmentStart)
        }
      }
    }
  }
}