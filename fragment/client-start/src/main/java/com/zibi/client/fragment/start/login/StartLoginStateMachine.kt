package com.zibi.client.fragment.start.login

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.zibi.mod.data_store.preferences.UserLogin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import com.zibi.client.fragment.start.login.StartLoginAction.TryLogin


sealed interface StartLoginState {
  data object Initialized : StartLoginState
  data object Skip : StartLoginState
  data object LoginScreen : StartLoginState
  data object DialogBadLogin : StartLoginState
}

sealed interface StartLoginAction {
  data object OKDialogBadLogin : StartLoginAction
  class TryLogin(
    val enterUserName: String,
    val enterPassword: String,
  ) : StartLoginAction
}

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class,
)
class StartLoginStateMachine constructor(
//    private val userLogin: UserLogin
) : FlowReduxStateMachine<StartLoginState, StartLoginAction>(
    initialState = StartLoginState.Initialized
), StartLoginNavigation {

  private val _navEvent: MutableSharedFlow<StartLoginNavigation.NavEvent> = MutableSharedFlow()
  override val navEvent: Flow<StartLoginNavigation.NavEvent> = _navEvent

  private var userName: String? = null
  private var password: String? = null

  init {
        spec {
            inState<StartLoginState.Initialized> {
              onEnter {
//                userName = userLogin.userName()
//                password = userLogin.password()
                var oneIsEmpty = true
                userName?.let { oneIsEmpty = it.isEmpty() }
                password?.let { oneIsEmpty = oneIsEmpty || it.isEmpty() }
                if( oneIsEmpty){
                  it.override { StartLoginState.Skip }
                }else{
                  it.override { StartLoginState.LoginScreen }
                }
              }
            }
          inState<StartLoginState.Skip> {
            onEnterEffect {
              _navEvent.emit(StartLoginNavigation.NavEvent.GoToStartMainScreen)
            }
          }
          inState<StartLoginState.LoginScreen> {
            on { action: TryLogin, state ->
              if(action.enterUserName == userName && action.enterPassword == password) {
                state.override { StartLoginState.Skip }
              }else{
                state.override { StartLoginState.DialogBadLogin }
              }
            }
          }
          inState<StartLoginState.DialogBadLogin> {
            on { _: StartLoginAction.OKDialogBadLogin, state ->
              state.override { StartLoginState.LoginScreen }
            }
          }
        }
    }
}