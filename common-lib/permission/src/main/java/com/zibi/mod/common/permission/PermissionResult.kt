package com.zibi.mod.common.permission

sealed class PermissionResult {
  object Granted : PermissionResult()
  data class NotGranted(val shouldShowRationale: Boolean) : PermissionResult()
  object UnknownStatus : PermissionResult()
}
