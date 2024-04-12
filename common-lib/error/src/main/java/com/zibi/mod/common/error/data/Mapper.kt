package com.zibi.mod.common.error.data

import com.zibi.mod.common.error.data.model.PayloadErrorDto
import com.zibi.mod.common.error.domain.PayloadErrorAction
import com.zibi.mod.common.error.domain.PayloadErrorCode
import com.zibi.mod.common.error.domain.PayloadErrorData

fun PayloadErrorDto.toDomain() = when (this) {
  is PayloadErrorDto.NewPayloadErrorDto -> PayloadErrorData(
    code = code,
    message = message,
    action = PayloadErrorAction.fromString(action),
    payloadErrorCode = PayloadErrorCode.UNSUPPORTED_ERROR
  )

  is PayloadErrorDto.OldPayloadErrorDtoV1 -> PayloadErrorData(
    code = null,
    message = error,
    action = PayloadErrorAction.UNKNOWN,
    payloadErrorCode = PayloadErrorCode.fromErrorCode(errorCode)
  )

  is PayloadErrorDto.OldPayloadErrorDtoV2 -> PayloadErrorData(
    code = null,
    message = description,
    action = PayloadErrorAction.UNKNOWN,
    payloadErrorCode = PayloadErrorCode.fromErrorCode(code)
  )
}


