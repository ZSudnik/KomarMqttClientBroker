package io.zibi.komar.broker.subscriptions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.text.ParseException
import java.util.Objects

class Topic(private val topic: String) : Serializable, Comparable<Topic> {

    @Transient
    private var tokens: List<Token>? = null

    @Transient
    private var valid: Boolean = false

    internal constructor(tokens: List<Token>):
    this(topic = tokens.map { obj: Token -> obj.toString() }.joinToString {"/"})
    {
        this.tokens = tokens
        valid = true
    }

    fun getTokens(): List<Token>? {
        if (tokens == null) {
            try {
                tokens = parseTopic(topic)
                valid = true
            } catch (e: ParseException) {
                valid = false
                LOG.error("Error parsing the topic: {}, message: {}", topic, e.message)
            }
        }
        return tokens
    }

    @Throws(ParseException::class)
    private fun parseTopic(topic: String): List<Token> {
        if (topic.isEmpty()) {
            throw ParseException(
                "Bad format of topic, topic MUST be at least 1 character [MQTT-4.7.3-1] and " +
                        "this was empty", 0
            )
        }
        val res: MutableList<Token> = mutableListOf()
        val splitted: MutableList<String> =
            topic.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
        if (splitted.isEmpty()) {
            res.add(Token.EMPTY)
        }
        if (topic.endsWith("/")) {
            // Add a fictious space
            splitted.add("")
        }
        for (i in splitted.indices) {
            val s: String = splitted[i]
            when {
                s.isEmpty()-> {
                    // if (i != 0) {
                    // throw new ParseException("Bad format of topic, expetec topic name between
                    // separators", i);
                    // }
                    res.add(Token.EMPTY)
                }
                s == "#" -> { // check that multi is the last symbol
                    if (i != splitted.size - 1)
                        throw ParseException("Bad format of topic, the multi symbol (#) has to be the last one after a separator", i)
                    res.add(Token.MULTI)
                }
                s.contains("#") ->
                    throw ParseException("Bad format of topic, invalid subtopic name: $s", i)
                s == "+" -> res.add(Token.SINGLE)
                s.contains("+") ->
                    throw ParseException("Bad format of topic, invalid subtopic name: $s", i)
                else -> res.add(Token((s)))
            }
        }
        return res
    }

    fun headToken(): Token? {
        val tokens: List<Token>? = getTokens()
        if (tokens!!.isEmpty()) {
            //TODO UGLY use Optional
            return null
        }
        return tokens[0]
    }

    val isEmpty: Boolean
        get() {
            val tokens: List<Token>? = getTokens()
            return tokens.isNullOrEmpty()
        }

    /**
     * @return a new Topic corresponding to this less than the head token
     */
    fun exceptHeadToken(): Topic {
        val tokens: List<Token>? = getTokens()
        if (tokens!!.isEmpty()) {
            return Topic(emptyList())
        }
        val tokensCopy: MutableList<Token> = tokens.toMutableList()
        tokensCopy.removeAt(0)
        return Topic(tokensCopy)
    }

    fun isValid(): Boolean {
        if (tokens == null) getTokens()
        return valid
    }

    fun isNoValid(): Boolean {
        if (tokens == null) getTokens()
        return !valid
    }
    /**
     * Verify if the 2 topics matching respecting the rules of MQTT Appendix A
     *
     * @param subscriptionTopic
     * the topic filter of the subscription
     * @return true if the two topics match.
     */
    // TODO reimplement with iterators or with queues
    fun match(subscriptionTopic: Topic): Boolean {
        val msgTokens: List<Token>? = getTokens()
        val subscriptionTokens: List<Token>? = subscriptionTopic.getTokens()
        var i = 0
        while (i < subscriptionTokens!!.size) {
            val subToken: Token = subscriptionTokens[i]
            if (Token.MULTI != subToken && Token.SINGLE != subToken) {
                if (i >= msgTokens!!.size) {
                    return false
                }
                val msgToken: Token = msgTokens[i]
                if (msgToken != subToken) {
                    return false
                }
            } else {
                if ((Token.MULTI == subToken)) {
                    return true
                }
                // if (Token.SINGLE.equals(subToken)) {
                //  skip a step forward
                // }
            }
            i++
        }
        return i == msgTokens!!.size
    }

    override fun toString(): String {
        return topic
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this.javaClass != other.javaClass) {
            return false
        }
        val other1: Topic = other as Topic
        return Objects.equals(topic, other1.topic)
    }

    override fun hashCode(): Int {
        return topic.hashCode()
    }

    override operator fun compareTo(other: Topic): Int {
        return topic.compareTo(other.topic)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Topic::class.java)

        /**
         * Factory method
         *
         * @param s the topic string (es "/a/b").
         * @return the created Topic instance.
         */
        fun asTopic(s: String): Topic {
            return Topic(s)
        }
    }
}
