package com.zibi.mod.common.ui.pager

import androidx.compose.runtime.Composable

data class TabRowItem(
  val title: String,
  val screen: @Composable () -> Unit,
)