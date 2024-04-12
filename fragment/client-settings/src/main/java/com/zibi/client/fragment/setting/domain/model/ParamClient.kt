package com.zibi.client.fragment.setting.domain.model

data class ParamClient(
    var mqttMyName: String = "",
    var mqttMyIdentifier: String = "",
) {
    override operator fun equals(other: Any?): Boolean =
        if (other is ParamClient)
            this.mqttMyName == other.mqttMyName && this.mqttMyIdentifier == other.mqttMyIdentifier
        else throw IllegalArgumentException("Can only compare to another class derived from ParamApp.")

    override fun hashCode(): Int {
        var result = mqttMyName.hashCode()
        result = 31 * result + mqttMyIdentifier.hashCode()
        return result
    }

}
