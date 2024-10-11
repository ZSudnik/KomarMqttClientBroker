package io.zibi.komar.mclient.utils

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.internal.ChunkBuffer
import io.ktor.utils.io.errors.IOException
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Connection(
    val socket: Socket,
    val input: ByteReadChannel,
    val output: ByteWriteChannel
){
    private val mutex = Mutex()
    suspend fun readByteArray(size: Int): ByteArray {
        if (input.isClosedForWrite) throw IOException()

        while (input.availableForRead < size) delay(50)
        val byteArray = ByteArray(size)
        mutex.withLock {
            input.readFully(byteArray, 0, size)
        }
        return byteArray
    }

    suspend fun readChunkBuffer(size: Int, chunkBuffer: ChunkBuffer): ChunkBuffer {
        chunkBuffer.reset()
        mutex.withLock {
            input.readFully(chunkBuffer, size)
        }
        return chunkBuffer
    }

    suspend fun writeChannel(byteArray: ByteArray) {
        if (output.isClosedForWrite) {
            throw IOException()
        }
        mutex.withLock {
            output.writeFully(byteArray)
        }
    }
}

/**
 * Opens socket input and output channels and returns connection object
 */
fun Socket.connection(): Connection = Connection(this, openReadChannel(), openWriteChannel(autoFlush = true))

suspend fun Connection?.waitForAvailable(time: Long = 5L): Boolean {
    if (this == null) return true
    return try {
        while(this.input.availableForRead <= 0){
            delay( time)
        }
        false
    } catch (ex: Exception) {
        true
    }
}
fun Connection?.isClose() = this?.input?.isClosedForWrite != false
