package io.zibi.codec.mqtt.util

import io.ktor.utils.io.bits.highByte
import io.ktor.utils.io.bits.lowByte
import kotlin.text.Charsets.UTF_8

 fun Boolean.toDecByteArray() = byteArrayOf( if(this) 0x01 else 0x00)

 fun Int.toDecByteArrayOne() = byteArrayOf( this.toByte())

fun Int.toDecByteArrayTwo() : ByteArray{
    val value = this.toShort()
    return byteArrayOf(value.highByte, value.lowByte)
}

 fun UShort.toDecByteArray() = byteArrayOf( ((this.toInt() shr 8) and 0xFF).toByte(), (this.toInt() and 0xFF).toByte() )

 fun Int.toDecByteArrayFour() = byteArrayOf(
    ((this shr 24) and 0xFF).toByte(), ((this shr 16) and 0xFF).toByte(),
    ((this shr 8) and 0xFF).toByte(), (this and 0xFF).toByte()
)

 fun String?.toDecByteArray(): ByteArray  {
    return this?.let {
        val size = byteArrayOf(((it.length shr 8) and 0xFF).toByte(), (it.length and 0xFF).toByte())
        size + it.toByteArray(UTF_8)
    } ?: byteArrayOf()
}

fun ByteArray?.toDecByteArray(): ByteArray  {
    return this?.let {
        val sizeBA =
            byteArrayOf(((it.size shr 8) and 0xFF).toByte(), (it.size and 0xFF).toByte())
        sizeBA + it
    } ?: byteArrayOf()
}

fun variableLengthInt( numInt: Int) : ByteArray{
    val listByte = mutableListOf<Byte>()
    var num = numInt
    do {
        var digit = num % 128
        num /= 128
        if (num > 0) {
            digit = digit or 0x80
        }
        listByte.add(digit.toByte())
    } while (num > 0)
    return listByte.toByteArray()
}