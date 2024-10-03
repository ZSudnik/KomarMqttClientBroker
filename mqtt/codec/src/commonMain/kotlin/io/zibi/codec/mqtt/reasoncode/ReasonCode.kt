package io.zibi.codec.mqtt.reasoncode

import java.util.Locale

abstract class ReasonCode(open val byteValue: UByte) {
    abstract fun toDecByteArray(): ByteArray
    abstract fun toDesc(): String

    protected fun makeDesc( desc: String?): String =
            desc?.let { it ->
                it.replace("_", " ")
                    .lowercase(Locale.getDefault())
                    .replaceFirstChar { it2 ->
                        if (it2.isLowerCase()) it2.titlecase(Locale.getDefault()) else it2.toString()
                    }
            } ?: "Error: no name"

    companion object {
//        @JvmStatic
        inline fun <reified T:ReasonCode> valueOf(b: UByte): T {
            return T::class.sealedSubclasses.mapNotNull { it.objectInstance as T }
                .find { it.byteValue == b }
                ?: throw IllegalArgumentException("unknown reason code: $b")
        }
    }
}