package io.zibi.komar.persistence

import org.h2.mvstore.WriteBuffer
import org.h2.mvstore.type.DataType
import java.nio.ByteBuffer

class ByteBufDataType : DataType {
    override fun compare(a: Any, b: Any): Int {
        return 0
    }

    override fun getMemory(obj: Any): Int {
        require(obj is ByteArray) { "Expected instance of ByteBuf but found " + obj.javaClass }
//        val payloadSize = obj.readableBytes()
//        return 4 + payloadSize
        return 4 + obj.size
    }

    override fun read(buff: ByteBuffer, obj: Array<Any>, len: Int, key: Boolean) {
        for (i in 0 until len) {
            obj[i] = read(buff)
        }
    }

    override fun write(buff: WriteBuffer, obj: Array<Any>, len: Int, key: Boolean) {
        for (i in 0 until len) {
            write(buff, obj[i])
        }
    }

    override fun read(buff: ByteBuffer): ByteArray {
        val payloadSize = buff.getInt()
        val payload = ByteArray(payloadSize)
        buff[payload]
//        return Unpooled.wrappedBuffer(payload)
        return buff.array()
    }

    override fun write(buff: WriteBuffer, obj: Any) {
        val casted = obj as ByteArray
        val payloadSize = casted.size
//        val payloadSize = casted.readableBytes()
        val rawBytes = casted.clone()
//        val rawBytes = ByteArray(payloadSize)
//        casted.copy().readBytes(rawBytes).release()
        buff.putInt(payloadSize)
        buff.put(rawBytes)
    }
}
