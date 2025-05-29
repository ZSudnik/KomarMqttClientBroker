package io.zibi.komar.broker.security

import io.zibi.komar.broker.subscriptions.Topic
import java.text.ParseException
import java.util.Locale

/**
 * Used by the ACLFileParser to push all authorizations it finds. ACLAuthorizator uses it in read
 * mode to check it topics matches the ACLs.
 *
 * Not thread safe.
 */
open class AuthorizationsCollector : IAuthorizatorPolicy {
    private var m_globalAuthorizations: MutableList<Authorization> = mutableListOf()
    private var m_patternAuthorizations: MutableList<Authorization> = mutableListOf()
    private var m_userAuthorizations: MutableMap<String, MutableList<Authorization>> = HashMap()
    private var m_parsingUsersSpecificSection = false
    private var m_parsingPatternSpecificSection = false
    private var m_currentUser = ""
    @Throws(ParseException::class)
    fun parse(line: String) {
        val acl = parseAuthLine(line)
            ?: // skip it's a user
            return
        if (m_parsingUsersSpecificSection) {
            // TODO in java 8 switch to m_userAuthorizations.putIfAbsent(m_currentUser, new List());
            if (!m_userAuthorizations.containsKey(m_currentUser)) {
                m_userAuthorizations[m_currentUser]= mutableListOf()
            }
            val userAuths = m_userAuthorizations[m_currentUser]!!
            userAuths.add(acl)
        } else if (m_parsingPatternSpecificSection) {
            m_patternAuthorizations.add(acl)
        } else {
            m_globalAuthorizations.add(acl)
        }
    }

    @Throws(ParseException::class)
    protected fun parseAuthLine(line: String): Authorization? {
        val tokens = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
        val keyword = tokens[0].lowercase(Locale.getDefault())
        return when (keyword) {
            "topic" -> createAuthorization(line, tokens)
            "user" -> {
                m_parsingUsersSpecificSection = true
                m_currentUser = tokens[1]
                m_parsingPatternSpecificSection = false
                null
            }

            "pattern" -> {
                m_parsingUsersSpecificSection = false
                m_currentUser = ""
                m_parsingPatternSpecificSection = true
                createAuthorization(line, tokens)
            }

            else -> throw ParseException(String.format("invalid line definition found %s", line), 1)
        }
    }

    @Throws(ParseException::class)
    private fun createAuthorization(line: String, tokens: List<String>): Authorization {
        if (tokens.size > 2) {
            // if the tokenized lines has 3 token the second must be the permission
            return try {
                val permission =
                    Authorization.Permission.valueOf(tokens[1].uppercase(Locale.getDefault()))
                // bring topic with all original spacing
                val topic = Topic(line.substring(line.indexOf(tokens[2])))
                Authorization(topic, permission)
            } catch (iaex: IllegalArgumentException) {
                throw ParseException("invalid permission token", 1)
            }
        }
        val topic = Topic(tokens[1])
        return Authorization(topic)
    }

    override fun canWrite(topic: Topic, user: String, client: String): Boolean {
        return canDoOperation(topic, Authorization.Permission.WRITE, user, client)
    }

    override fun canRead(topic: Topic, user: String, client: String): Boolean {
        return canDoOperation(topic, Authorization.Permission.READ, user, client)
    }

    private fun canDoOperation(
        topic: Topic,
        permission: Authorization.Permission,
        username: String,
        client: String
    ): Boolean {
        if (matchACL(m_globalAuthorizations, topic, permission)) {
            return true
        }
        if (isNotEmpty(client) || isNotEmpty(username)) {
            for (auth in m_patternAuthorizations) {
                val substitutedTopic =
                    Topic(auth.topic.toString().replace("%c", client).replace("%u", username))
                if (auth.grant(permission)) {
                    if (topic.match(substitutedTopic)) {
                        return true
                    }
                }
            }
        }
        if (isNotEmpty(username)) {
            if (m_userAuthorizations.containsKey(username)) {
                val auths: List<Authorization> = m_userAuthorizations[username]!!
                if (matchACL(auths, topic, permission)) {
                    return true
                }
            }
        }
        return false
    }

    private fun matchACL(
        auths: List<Authorization>,
        topic: Topic,
        permission: Authorization.Permission
    ): Boolean {
        for (auth in auths) {
            if (auth.grant(permission)) {
                if (topic.match(auth.topic)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isNotEmpty(client: String?): Boolean {
        return !client.isNullOrEmpty()
    }

    val isEmpty: Boolean
        get() = m_globalAuthorizations.isEmpty()

    companion object {
        fun emptyImmutableCollector(): AuthorizationsCollector {
            val coll = AuthorizationsCollector()
            coll.m_globalAuthorizations = emptyList<Authorization>().toMutableList()
            coll.m_patternAuthorizations = emptyList<Authorization>().toMutableList()
            coll.m_userAuthorizations = emptyMap<String, MutableList<Authorization>>().toMutableMap()
            return coll
        }
    }
}
