package com.zibi.mod.common.error.domain

class PayloadErrorData(
  val code: String?,
  val message: String?,
  val action: PayloadErrorAction,
  val payloadErrorCode: PayloadErrorCode,
)
