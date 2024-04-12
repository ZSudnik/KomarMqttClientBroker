package com.zibi.client.fragment.setting.domain.model

data class ParamApp(
    var userName: String = "",
    var password: String = "",
){

    override operator fun equals(other: Any?): Boolean =
        if (other is ParamApp)
            this.userName == other.userName && this.password == other.password
        else
            throw IllegalArgumentException("Can only compare to another class derived from ParamApp.")

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }

}
