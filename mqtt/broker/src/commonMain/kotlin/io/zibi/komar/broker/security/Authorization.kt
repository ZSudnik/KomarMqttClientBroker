package io.zibi.komar.broker.security

import io.zibi.komar.broker.subscriptions.Topic

/**
 * Carries the read/write authorization to topics for the users.
 */
class Authorization internal constructor(
    val topic: Topic,
    protected val permission: Permission = Permission.READWRITE
) {
    /**
     * Access rights
     */
    enum class Permission {
        READ,
        WRITE,
        READWRITE
    }

    fun grant(desiredPermission: Permission): Boolean {
        return permission == desiredPermission || permission == Permission.READWRITE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Authorization
        if (permission != that.permission) return false
        return topic == that.topic
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + permission.hashCode()
        return result
    }
}
