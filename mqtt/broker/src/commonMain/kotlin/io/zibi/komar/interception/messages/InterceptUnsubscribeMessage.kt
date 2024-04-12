package io.zibi.komar.interception.messages

class InterceptUnsubscribeMessage(
    val topicFilter: String,
    val clientID: String,
    val username: String
) : InterceptMessage
