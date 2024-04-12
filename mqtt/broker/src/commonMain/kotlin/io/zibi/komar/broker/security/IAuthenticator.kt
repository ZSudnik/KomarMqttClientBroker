package io.zibi.komar.broker.security

/**
 * username and password checker
 */
interface IAuthenticator {
    fun checkValid(clientId: String?, username: String?, password: ByteArray?): Boolean
}
