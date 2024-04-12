package com.zibi.mod.common.ui.snackBar

sealed interface SnackBarState {
  data class Visible(val snackBarData: SnackBarData) : SnackBarState
  object Hidden : SnackBarState
}
