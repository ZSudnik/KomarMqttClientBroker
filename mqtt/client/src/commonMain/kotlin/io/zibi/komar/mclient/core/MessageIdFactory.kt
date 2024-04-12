package io.zibi.komar.mclient.core


object MessageIdFactory {
    private val using = mutableMapOf<Int, Int>()
    private var lastId = 0
    @Throws(Exception::class)
    fun get(): Int {
        synchronized(using) {
            var id = lastId
            for (i in 1..65535) {
                ++id
                if (id < 1 || id > 65535){
                    id = 1
                    using.clear()
                }
                if (!using.contains(id)) {
                    using[id] = id
                    lastId = id
                    return id
                }
            }
            throw Exception("The message id has been used up!")
        }
    }

    fun release(id: Int) {
        if (id > 0) synchronized(using) { using.remove(id) }
    }
}