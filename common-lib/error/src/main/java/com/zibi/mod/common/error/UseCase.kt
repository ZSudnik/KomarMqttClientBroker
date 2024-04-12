package com.zibi.mod.common.error

interface UseCase<PARAMS, RESULT> {

  suspend operator fun invoke(params: PARAMS): com.zibi.mod.common.error.Either<com.zibi.mod.common.error.DomainError, RESULT>
}
