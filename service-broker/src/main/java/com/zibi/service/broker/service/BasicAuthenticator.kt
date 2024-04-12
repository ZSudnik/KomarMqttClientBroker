package com.zibi.service.broker.service

import io.zibi.komar.broker.config.IConfig
import io.zibi.komar.broker.security.IAuthenticator

class BasicAuthenticator(config: IConfig) : IAuthenticator {

    private val storedUserName: String = config.getProperty(USERNAME, "")
    private val storedPassword: String = config.getProperty(PASSWORD, "")

    override fun checkValid(clientId: String?, username: String?, password: ByteArray?): Boolean {
        if (username == null || password == null) {
            return false
        }

        return username == storedUserName && password.contentEquals(storedPassword.toByteArray())
    }

    companion object {
        const val USERNAME = "narada_username"
        const val PASSWORD = "narada_password"
    }
}