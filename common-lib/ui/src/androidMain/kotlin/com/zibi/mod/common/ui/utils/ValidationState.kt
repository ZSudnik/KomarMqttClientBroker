package com.zibi.mod.common.ui.utils

sealed class ValidationState {
  object Default : ValidationState()
  object Valid : ValidationState()
  data class Invalid(val message: String = "") : ValidationState()

  fun isValid() = this == Valid
}