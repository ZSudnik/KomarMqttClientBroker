package com.zibi.mod.common.error

sealed class Either<out A, out B> {
  class Left<A>(val value: A) : com.zibi.mod.common.error.Either<A, Nothing>()
  class Right<B>(val value: B) : com.zibi.mod.common.error.Either<Nothing, B>()

  suspend fun <RESULT> suspendExecute(
    onSuccess: suspend (B) -> RESULT,
    onFailure: suspend (A) -> RESULT,
  ): RESULT = when (this) {
    is com.zibi.mod.common.error.Either.Left -> onFailure(value)
    is com.zibi.mod.common.error.Either.Right -> onSuccess(value)
  }

  fun <RESULT> execute(
    onSuccess: (B) -> RESULT,
    onFailure: (A) -> RESULT,
  ): RESULT = when (this) {
    is com.zibi.mod.common.error.Either.Left -> onFailure(value)
    is com.zibi.mod.common.error.Either.Right -> onSuccess(value)
  }
}
