package io.zibi.codec.mqtt.reasoncode

import java.util.Locale

interface ReasonCode {
    fun toDecByteArray(): ByteArray
    fun toDesc(): String

    companion object{
        fun makeDesc( desc: String): String =
            desc.replace("_", " ")
                .lowercase(Locale.getDefault())
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        }
}