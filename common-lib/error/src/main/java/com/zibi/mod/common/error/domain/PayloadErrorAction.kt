package com.zibi.mod.common.error.domain

enum class PayloadErrorAction {
  INFO,
  LOGOUT,
  DEACTIVATE,
  UNKNOWN,
  ;

  companion object {
    private const val INFO_VALUE = "INFO"
    private const val LOGOUT_VALUE = "LOGOUT"
    private const val DEACTIVATE_VALUE = "DEACTIVATE"

    internal fun fromString(errorCode: String?) = when (errorCode) {
      INFO_VALUE -> INFO
      LOGOUT_VALUE -> LOGOUT
      DEACTIVATE_VALUE -> DEACTIVATE
      else -> UNKNOWN
    }
  }
}
