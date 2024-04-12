package com.zibi.client.fragment.setting.main

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.mad.statemachine.StateMachine
import com.zibi.client.fragment.setting.domain.model.ParamAll
import com.zibi.client.fragment.setting.domain.model.ParamApp
import com.zibi.client.fragment.setting.domain.model.ParamBroker
import com.zibi.client.fragment.setting.domain.model.ParamClient
import com.zibi.mod.common.navigation.coroutines.MutableColdSharedFlow
import com.zibi.mod.data_store.preferences.ClientSetting
import com.zibi.mod.data_store.preferences.UserLogin
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.State
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.Action
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.Navigation
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.Navigation.NavEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

interface SettingBrokerStateMachine : StateMachine<State, Action> {

    sealed interface State {
        data object Loading: State
        data class ContentState(
            val paramAll: ParamAll
        ) : State
    }

    sealed interface Action {
        data class GoToBack(
            val paramApp: ParamApp?,
            val paramBroker: ParamBroker?,
            val paramClient: ParamClient?,
        ) : Action
        data object Empty : Action
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
    private val clientSetting: ClientSetting
) : FlowReduxStateMachine<State, Action>(
    initialState = State.Loading
    ),
    Navigation, SettingBrokerStateMachine {

    override val navEvent = MutableColdSharedFlow<NavEvent>()

     init {
        spec {
            inState<State.Loading> {
                onEnter { state ->
                    val items = loadParam()
                    state.override { State.ContentState(items) }
                }
            }
            inState<State.ContentState> {
                onActionEffect<Action.GoToBack> { action, _ ->
                    action.paramBroker?.let {
                        clientSetting.setMqttBrokerUsername(it.mqttUserName)
                        clientSetting.setMqttBrokerPassword(it.mqttPassword)
                        clientSetting.setMqttBrokerPort(it.mqttPort)
                        clientSetting.setMqttBrokerHost( it.mqttIpAddress)
                        clientSetting.setWSEnabled(it.webSocketEnabled)
                        clientSetting.setWSPort(it.webSocketPort)
                        clientSetting.setWSPath(it.webSocketPath)
                        clientSetting.setAuthEnabled(it.authenticationEnabled)
                    }
                    action.paramApp?.let {
                        userLogin.setUsername(it.userName)
                        userLogin.setPassword(it.password)
                    }
                    action.paramClient?.let {
                        clientSetting.setMqttMyName(it.mqttMyName)
                        clientSetting.setMqttMyIdentifier(it.mqttMyIdentifier)
                    }
                    navEvent.emit(NavEvent.Back)
                }
            }
        }
    }

    private suspend fun loadParam(): ParamAll {
        val paramApp = ParamApp(
            userName = userLogin.userName(),
            password = userLogin.password()
        )
        val paramClient = ParamClient(
            mqttMyName = clientSetting.mqttMyNameFirst(),
                    mqttMyIdentifier = clientSetting.mqttMyIdentifierFirst(),
        )
        val paramClientDefault = ParamClient(
            mqttMyName = ClientSetting.MQTT_CLIENT_USERNAME_DEFAULT,
            mqttMyIdentifier = ClientSetting.MQTT_CLIENT_IDENTIFIER_DEFAULT,
        )
        val paramBroker = ParamBroker(
            mqttPassword = clientSetting.mqttBrokerPasswordFirst(),
                    mqttUserName = clientSetting.mqttBrokerUserNameFirst(),
                    mqttIpAddress = clientSetting.mqttBrokerHostFirst(),
                    mqttPort = clientSetting.mqttBrokerPortFirst(),
                    webSocketEnabled = clientSetting.wsEnabledFirst(),
                    webSocketPort = clientSetting.wsPortFirst(),
                    webSocketPath = clientSetting.wsPathFirst(),
                    authenticationEnabled = clientSetting.authEnabledFirst(),
        )
        val paramBrokerDefault = ParamBroker(
            mqttPassword = ClientSetting.MQTT_BROKER_PASSWORD_DEFAULT,
            mqttUserName = ClientSetting.MQTT_BROKER_USERNAME_DEFAULT,
            mqttIpAddress = ClientSetting.MQTT_HOST_DEFAULT,
            mqttPort = ClientSetting.MQTT_PORT_DEFAULT,
            webSocketEnabled = ClientSetting.WS_ENABLED_DEFAULT,
            webSocketPort = ClientSetting.WS_PORT_DEFAULT,
            webSocketPath = ClientSetting.WS_PATH_DEFAULT,
            authenticationEnabled = ClientSetting.AUTH_ENABLED_DEFAULT,
        )
        return ParamAll(
            paramApp = paramApp,
            paramClient = paramClient,
            paramClientDefault = paramClientDefault,
            paramBroker = paramBroker,
            paramBrokerDefault = paramBrokerDefault
        )
    }
}