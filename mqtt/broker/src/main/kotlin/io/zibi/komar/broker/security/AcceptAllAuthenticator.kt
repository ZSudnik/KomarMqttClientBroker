package io.zibi.komar.broker.security

class AcceptAllAuthenticator : IAuthenticator {
    override fun checkValid(clientId: String?, username: String?, password: ByteArray?): Boolean {
        return true
    }
}
