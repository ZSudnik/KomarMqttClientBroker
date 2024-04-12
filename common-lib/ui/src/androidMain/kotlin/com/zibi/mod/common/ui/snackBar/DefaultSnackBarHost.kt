package com.zibi.mod.common.ui.snackBar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun DefaultSnackBarHost(snackbarHostState: SnackbarHostState) =
  SnackbarHost(
    hostState = snackbarHostState,
    modifier = Modifier.padding(AppTheme.dimensions.regularPadding),
  ) { data ->
    SnackBar(
      text = data.visuals.message,
      actionText = data.visuals.actionLabel,
      actionIconResId = R.drawable.common_ui_ic_close_bold,
      onActionClick = data::performAction
    )
  }
