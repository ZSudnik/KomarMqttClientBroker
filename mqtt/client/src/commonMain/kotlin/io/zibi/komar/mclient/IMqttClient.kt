package io.zibi.komar.mclient

interface IMqttClient {


    @Throws(Exception::class)
    suspend fun connectAuto(timeout: Long? = null)

    suspend fun connectOne(timeout: Long? = null)


    @Throws(Exception::class)
    fun subscribe(topics: List<String>)

    /**
     * @param qos
     * @param topics
     * @throws Exception
     */
    @Throws(Exception::class)
    fun subscribe(qos: Int, topics: List<String>)

    @Throws(Exception::class)
    fun unsubscribe(topics: List<String>)

    @Throws(Exception::class)
    fun publish(topic: String, content: String)

    fun shutDown()

}