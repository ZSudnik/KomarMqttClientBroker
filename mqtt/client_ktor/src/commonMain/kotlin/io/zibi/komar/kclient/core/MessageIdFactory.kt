package io.zibi.komar.mclient.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

object MessageIdFactory {
    private val using =  ConcurrentHashMap<Int, Int>()
    private var lastId = 0
    private val mutex = Mutex()

    @Throws(Exception::class)
    suspend fun get(): Int {
        mutex.withLock{
            var id = lastId
            do{
                ++id
                if (id < 1 || id >= 65535){
                    id = 1
                    using.clear()
                }
                if (!using.contains(id)) {
                    using[id] = id
                    lastId = id
                    return id
                }
            }while (id < 65535)
            throw Exception("The message id has been used up!")
        }
    }

    suspend fun release(id: Int) {
        mutex.withLock { using.remove(id) }
    }
}