package io.zibi.komar.broker.security

import io.zibi.komar.broker.subscriptions.Topic

class DenyAllAuthorizatorPolicy : IAuthorizatorPolicy {
    override fun canRead(topic: Topic, user: String, client: String): Boolean {
        return false
    }

    override fun canWrite(topic: Topic, user: String, client: String): Boolean {
        return false
    }
}
