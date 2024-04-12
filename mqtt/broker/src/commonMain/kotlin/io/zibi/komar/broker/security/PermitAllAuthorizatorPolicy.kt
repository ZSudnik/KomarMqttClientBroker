package io.zibi.komar.broker.security

import io.zibi.komar.broker.subscriptions.Topic

class PermitAllAuthorizatorPolicy : IAuthorizatorPolicy {
    override fun canWrite(topic: Topic, user: String, client: String): Boolean {
        return true
    }

    override fun canRead(topic: Topic, user: String, client: String): Boolean {
        return true
    }
}
