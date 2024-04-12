package com.zibi.mod.fragment.broker.domain.model

data class ParamBroker(
    var password: String = "",
    var userName: String = "",
    var mqttPort: Int = -1,
    var webSocketEnabled: Boolean = false,
    var webSocketPort: Int = -1,
    var webSocketPath: String = "",
    var authenticationEnabled: Boolean = false,
) {
    override operator fun equals(other: Any?): Boolean =
        if (other is ParamBroker)
            this.userName == other.userName &&
                    this.password == other.password &&
                    this.userName == other.userName &&
                    this.mqttPort == other.mqttPort &&
                    this.webSocketEnabled == other.webSocketEnabled &&
                    this.webSocketPort == other.webSocketPort &&
                    this.webSocketPath == other.webSocketPath &&
                    this.authenticationEnabled == other.authenticationEnabled
        else throw IllegalArgumentException("Can only compare to another class derived from ParamApp.")

    override fun hashCode(): Int {
        var result = password.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + mqttPort
        result = 31 * result + webSocketEnabled.hashCode()
        result = 31 * result + webSocketPort
        result = 31 * result + webSocketPath.hashCode()
        result = 31 * result + authenticationEnabled.hashCode()
        return result
    }

}
