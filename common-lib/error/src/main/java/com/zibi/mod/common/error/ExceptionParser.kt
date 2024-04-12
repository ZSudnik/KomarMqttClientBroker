package com.zibi.mod.common.error

interface ExceptionParser<T : com.zibi.mod.common.error.DomainError> {
  fun parse(e: Exception): com.zibi.mod.common.error.Either<Exception, T>
}
