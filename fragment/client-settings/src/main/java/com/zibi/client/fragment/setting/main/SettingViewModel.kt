package com.zibi.client.fragment.setting.main

import androidx.compose.runtime.Composable
import com.zibi.client.fragment.setting.R
import kotlinx.coroutines.flow.Flow
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.Action
import com.zibi.client.fragment.setting.main.SettingBrokerStateMachine.Navigation
import com.zibi.mod.common.resources.StringResolver
import com.zibi.client.fragment.setting.domain.model.SettingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

interface SettingMainViewModel {
  val uiData: () -> SettingData
  fun onBack() = Unit
  @Composable
  fun rememberState(): androidx.compose.runtime.State<SettingBrokerStateMachine.State?>
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SettingViewModelImpl(
    private val stateMachine: SettingStateMachineImp,
    private val stringResolver: StringResolver,
) : AbsStateViewModel<SettingBrokerStateMachine.State, Action>(stateMachine),
    SettingMainViewModel, Navigation {

  override val navEvent: Flow<Navigation.NavEvent>
    get() = stateMachine.navEvent

  @Composable
  override fun rememberState(): androidx.compose.runtime.State<SettingBrokerStateMachine.State?> =
    super.remState()

  override val uiData: () -> SettingData
  get() = { SettingData(
      topMenuTitle = stringResolver.getString(R.string.fragment_setting_top_bar_title),
      sectionAppTitle = stringResolver.getString(R.string.fragment_setting_section_app_title),
      sectionBrokerTitle = stringResolver.getString(R.string.fragment_setting_section_broker_title),
      sectionClientTitle = stringResolver.getString(R.string.fragment_setting_section_client_title),
      descClientMyName = stringResolver.getString(R.string.fragment_setting_desc_mqtt_client_name),
      descClientMyIdentifier = stringResolver.getString(R.string.fragment_setting_desc_mqtt_client_identification),
      descAppUserName = stringResolver.getString(R.string.fragment_setting_desc_app_user_name),
      descAppPassword = stringResolver.getString(R.string.fragment_setting_desc_app_password),
      descBrokerUserName = stringResolver.getString(R.string.fragment_setting_desc_mqtt_broker_user_name),
      descBrokerPassword = stringResolver.getString(R.string.fragment_setting_desc_mqtt_broker_password),
      descBrokerMqttPort = stringResolver.getString(R.string.fragment_setting_desc_mqtt_port),
      descBrokerMqqtIpAddress = stringResolver.getString(R.string.fragment_setting_desc_mqtt_ipaddress),
      descBrokerWebSocketEnabled = stringResolver.getString(R.string.fragment_setting_desc_web_socket_enabled),
      descBrokerWebSocketPort = stringResolver.getString(R.string.fragment_setting_desc_web_socket_port),
      descBrokerWebSocketPath = stringResolver.getString(R.string.fragment_setting_desc_web_socket_path),
      descBrokerAuthenticationEnabled = stringResolver.getString(R.string.fragment_setting_desc_authentication_enabled),
      textPortWarning = stringResolver.getString(R.string.fragment_setting_port_warning),
      textEmptyWarning = stringResolver.getString(R.string.fragment_setting_empty_warning),
      dialogTitle = stringResolver.getString(R.string.fragment_setting_dialog_title),
      dialogContent = stringResolver.getString(R.string.fragment_setting_dialog_content),
      dialogPositiveButtonLabel = stringResolver.getString(R.string.fragment_setting_dialog_positive_button),
      dialogNegativeButtonLabel = stringResolver.getString(R.string.fragment_setting_dialog_negative_button),
      dialogCancelButtonLabel = stringResolver.getString(R.string.fragment_setting_dialog_cancel_button),
      onBack = { paramApp, paramBroker, paramClient ->
          super.dispatch(action = Action.GoToBack(paramApp, paramBroker, paramClient))},
      )

  }


  override fun onBack() {
//    super.dispatch(Action.GoToBack)
  }

}