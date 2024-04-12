package com.zibi.mod.common.ui.snackBar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class SnackBarData(
  override val message: String,
  override val actionLabel: String? = null,
  override val duration: SnackbarDuration = SnackbarDuration.Indefinite,
  override val withDismissAction: Boolean = true
) : SnackbarVisuals
