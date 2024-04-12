package com.zibi.mod.common.ui.snackBar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface SnackBarManagerStateHolder {
  val snackBarVisibilityState: Flow<SnackBarState>
  fun showSnackBar(snackBarData: SnackBarData)
  fun hideSnackBar()
}

class SnackBarManagerStateHolderImpl constructor() : SnackBarManagerStateHolder {
  private val _snackBarVisibilityState = MutableStateFlow<SnackBarState>(SnackBarState.Hidden)
  override val snackBarVisibilityState: Flow<SnackBarState> = _snackBarVisibilityState

  override fun showSnackBar(snackBarData: SnackBarData) {
    _snackBarVisibilityState.value = SnackBarState.Visible(snackBarData)
  }

  override fun hideSnackBar() {
    _snackBarVisibilityState.value = SnackBarState.Hidden
  }
}
