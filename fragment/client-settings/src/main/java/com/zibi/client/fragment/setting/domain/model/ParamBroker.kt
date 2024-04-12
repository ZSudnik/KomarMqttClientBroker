package com.zibi.client.fragment.setting.domain.model

data class ParamBroker(
    var mqttPassword: String = "",
    var mqttUserName: String = "",
    var mqttIpAddress: String = "",
    var mqttPort: Int = -1,
    var webSocketEnabled: Boolean = false,
    var webSocketPort: Int = -1,
    var webSocketPath: String = "",
    var authenticationEnabled: Boolean = false,
) {
    override operator fun equals(other: Any?): Boolean =
        if (other is ParamBroker)
            this.mqttUserName == other.mqttUserName &&
                    this.mqttPassword == other.mqttPassword &&
                    this.mqttIpAddress == other.mqttIpAddress &&
                    this.mqttPort == other.mqttPort &&
                    this.webSocketEnabled == other.webSocketEnabled &&
                    this.webSocketPort == other.webSocketPort &&
                    this.webSocketPath == other.webSocketPath &&
                    this.authenticationEnabled == other.authenticationEnabled
        else throw IllegalArgumentException("Can only compare to another class derived from ParamApp.")

    override fun hashCode(): Int {
        var result = mqttPassword.hashCode()
        result = 31 * result + mqttUserName.hashCode()
        result = 31 * result + mqttPort
        result = 31 * result + mqttIpAddress.hashCode()
        result = 31 * result + webSocketEnabled.hashCode()
        result = 31 * result + webSocketPort
        result = 31 * result + webSocketPath.hashCode()
        result = 31 * result + authenticationEnabled.hashCode()
        return result
    }

}
