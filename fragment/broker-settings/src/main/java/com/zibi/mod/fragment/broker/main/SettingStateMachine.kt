package com.zibi.mod.fragment.broker.main

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.mad.statemachine.StateMachine
import com.zibi.mod.common.navigation.coroutines.MutableColdSharedFlow
import com.zibi.mod.data_store.preferences.BrokerSetting
import com.zibi.mod.data_store.preferences.UserLogin
import com.zibi.mod.fragment.broker.domain.model.ParamApp
import com.zibi.mod.fragment.broker.domain.model.ParamBroker
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.State
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.Action
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.Navigation
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.Navigation.NavEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

interface SettingBrokerStateMachine : StateMachine<State, Action> {

    sealed interface State {
        data object Loading: State
        data class ContentState(
         val paramApp: ParamApp,
         val paramBroker: ParamBroker,
         val paramDefaultBroker: ParamBroker,
        ) : State
    }

    sealed interface Action {
        data class GoToBack(
            val paramApp: ParamApp?,
            val paramBroker: ParamBroker?,
        ) : Action
    }

    interface Navigation {
        sealed interface NavEvent {
            data object Back : NavEvent
            data object FragmentTwo : NavEvent
        }
        val navEvent: Flow<NavEvent>
    }

}

@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class,
)
class SettingStateMachineImp(
    private val userLogin: UserLogin,
    private val brokerSetting: BrokerSetting
) : FlowReduxStateMachine<State, Action>(initialState = State.Loading),
    Navigation, SettingBrokerStateMachine {

    override val navEvent = MutableColdSharedFlow<NavEvent>()

     init {
        spec {
            inState<State.Loading> {
                onEnter {
                    val paramApp = ParamApp(
                        userName = userLogin.userName(),
                        password = userLogin.password()
                    )
                    val paramBroker = ParamBroker(
                        password = brokerSetting.passwordFirst(),
                        userName = brokerSetting.userNameFirst(),
                        mqttPort = brokerSetting.mqttPortFirst(),
                        webSocketEnabled = brokerSetting.wsEnabledFirst(),
                        webSocketPort = brokerSetting.wsPortFirst(),
                        webSocketPath = brokerSetting.wsPathFirst(),
                        authenticationEnabled = brokerSetting.authEnabledFirst(),
                    )
                    val paramDefaultBroker = ParamBroker(
                        password = BrokerSetting.PWD_DEFAULT,
                        userName = BrokerSetting.UNAME_DEFAULT,
                        mqttPort = BrokerSetting.MQTT_PORT_DEFAULT,
                        webSocketEnabled = BrokerSetting.WS_ENABLED_DEFAULT,
                        webSocketPort = BrokerSetting.WS_PORT_DEFAULT,
                        webSocketPath = BrokerSetting.WS_PATH_DEFAULT,
                        authenticationEnabled = BrokerSetting.AUTH_ENABLED_DEFAULT,
                    )
                    it.override {
                        State.ContentState(
                            paramApp = paramApp,
                            paramBroker = paramBroker,
                            paramDefaultBroker = paramDefaultBroker,
                        )
                    }
                }
            }
            inState<State.ContentState> {
                onActionEffect<Action.GoToBack> { action, _ ->
                    action.paramBroker?.let {
                        brokerSetting.setUsername(it.userName)
                        brokerSetting.setPassword(it.password)
                        brokerSetting.setMqttPort(it.mqttPort)
                        brokerSetting.setWSEnabled(it.webSocketEnabled)
                        brokerSetting.setWSPort(it.webSocketPort)
                        brokerSetting.setWSPath(it.webSocketPath)
                        brokerSetting.setAuthEnabled(it.authenticationEnabled)
                    }
                    action.paramApp?.let {
                        userLogin.setUsername(it.userName)
                        userLogin.setPassword(it.password)
                    }
                    navEvent.emit(NavEvent.Back)
                }
            }
        }
    }
}
