package io.zibi.komar.broker.subscriptions

/**
 * Internal use only class.
 */
class Token(val name: String) {

    fun name(): String {
        return name
    }

    fun match(t: Token): Boolean {
        if (t in listOf(MULTI, SINGLE)) {
            return false
        }
        if (this in listOf(MULTI, SINGLE)) {
            return true
        }
        return equals(t)
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 29 * hash + name.hashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val other1: Token = other as Token
        return (name == other1.name)
    }

    override fun toString(): String {
        return name
    }

    companion object {
        val EMPTY: Token = Token("")
        val MULTI: Token = Token("#")
        val SINGLE: Token = Token("+")
    }
}
