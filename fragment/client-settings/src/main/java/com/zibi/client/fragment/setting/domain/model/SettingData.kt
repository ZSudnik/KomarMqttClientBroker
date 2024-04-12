package com.zibi.client.fragment.setting.domain.model


data class SettingData(
    val topMenuTitle: String,
    val sectionAppTitle: String,
    val sectionClientTitle: String,
    val descClientMyName: String,
    val descClientMyIdentifier: String,
    val sectionBrokerTitle: String,
    val descAppUserName: String,
    val descAppPassword: String,
    val descBrokerUserName: String,
    val descBrokerPassword: String,
    val descBrokerMqttPort: String,
    val descBrokerMqqtIpAddress: String,
    val descBrokerWebSocketEnabled: String,
    val descBrokerWebSocketPort: String,
    val descBrokerWebSocketPath: String,
    val descBrokerAuthenticationEnabled: String,
    val textPortWarning: String,
    val textEmptyWarning: String,
    val dialogTitle: String,
    val dialogContent: String,
    val dialogPositiveButtonLabel: String,
    val dialogNegativeButtonLabel: String,
    val dialogCancelButtonLabel: String,
    val onBack: (paramApp: ParamApp?, paramBroker: ParamBroker?, paramClient: ParamClient?) -> Unit,
    )
