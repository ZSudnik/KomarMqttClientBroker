package io.zibi.komar.logging

import io.zibi.komar.interception.InterceptHandler

object LoggingUtils {
    fun <T : InterceptHandler?> getInterceptorIds(handlers: Collection<T>): Collection<String> {
        val result: MutableCollection<String> = mutableListOf()
        handlers.forEach { handler ->
            result.add(handler!!.getID())
        }
        return result
    }
}
