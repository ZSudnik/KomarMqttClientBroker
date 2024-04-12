package com.zibi.mod.common.ui.confirmation.summary

data class ConfirmationScreenContent(
  val title: String,
  val description: String,
  val isImportant: Boolean = false,
  val isHighlighted: Boolean = false,
)

sealed class Status(val title: String) {
  class Success(title: String) : Status(title)
  class Warning(title: String) : Status(title)
  class InProgress(title: String) : Status(title)
  class Info(title: String) : Status(title)
}
