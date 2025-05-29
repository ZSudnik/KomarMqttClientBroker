package io.zibi.komar.broker.subscriptions


interface ISubscriptionsDirectory {
    fun listAllSessionIds(): Set<String>
    fun matchWithoutQosSharpening(topic: Topic): List<Subscription?>?
    fun matchQosSharpening(topic: Topic): List<Subscription>
    fun add(newSubscription: Subscription)
    fun removeSubscription(topic: Topic, clientID: String)
    fun size(): Int
    fun dumpTree(): String?
}
