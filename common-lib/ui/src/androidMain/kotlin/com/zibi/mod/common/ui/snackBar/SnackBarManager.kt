
package com.zibi.mod.common.ui.snackBar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SnackBarManager(
  snackbarHostState: SnackbarHostState,
  snackBarState: SnackBarState,
  onSnackBarHidden: () -> Unit,
  onDismissed: () -> Unit = {},
  onActionPerformed: () -> Unit = {},
) {
  LaunchedEffect(key1 = snackBarState) {
    when (snackBarState) {
      is SnackBarState.Visible -> snackbarHostState.showSnackbar(visuals = snackBarState.snackBarData).let {
        when (it) {
          SnackbarResult.Dismissed -> onDismissed()
          SnackbarResult.ActionPerformed -> onActionPerformed()
        }
        onSnackBarHidden()
      }

      SnackBarState.Hidden -> snackbarHostState.currentSnackbarData?.dismiss()
    }
  }
}
