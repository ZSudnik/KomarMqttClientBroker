package com.zibi.mod.common.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.pager.PagerState
import com.zibi.mod.common.ui.theme.AppTheme

@OptIn( ExperimentalFoundationApi::class)
@Composable
fun PagerTabRowIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
  val tabWidth = tabPositions[pagerState.currentPage].width
  Box(
    modifier = Modifier
      .offset(
        x = tabPositions[pagerState.currentPage].left
          .plus(tabPositions[pagerState.currentPage].width * pagerState.currentPageOffsetFraction)
      ).wrapContentSize(align = Alignment.BottomStart)
      .width(tabWidth)
      .padding(AppTheme.dimensions.xSmallPadding)
      .fillMaxSize()
      .background(color = Color.White, RoundedCornerShape(7.dp))
      .zIndex(1f)
  )
}