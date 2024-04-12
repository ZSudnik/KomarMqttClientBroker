package com.zibi.mod.common.ui.dialog.small

sealed class SmallDialogType(
  val title: String,
  val content: String? = null,
  val positiveButtonText: String? = null,
  val negativeButtonText: String? = null,
  val cancelButtonText: String? = null,
  val onPositiveButtonClick: (() -> Unit)? = null,
  val onNegativeButtonClick: (() -> Unit)? = null,
  val onCancelButtonClick: (() -> Unit)? = null,
) {
  class Default(
    title: String,
    content: String,
    positiveButtonText: String,
    negativeButtonText: String? = null,
    cancelButtonText: String? = null,
    onPositiveButtonClick: (() -> Unit)? = null,
    onNegativeButtonClick: (() -> Unit)? = null,
    onCancelButtonClick: (() -> Unit)? = null,
  ) : SmallDialogType(
    title = title,
    content = content,
    positiveButtonText = positiveButtonText,
    negativeButtonText = negativeButtonText,
    cancelButtonText = cancelButtonText,
    onPositiveButtonClick = onPositiveButtonClick,
    onNegativeButtonClick = onNegativeButtonClick,
    onCancelButtonClick = onCancelButtonClick,
  )
}