package com.zibi.mod.common.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.launch
import com.zibi.mod.common.ui.theme.AppTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerTabRow(
  pages: List<TabRowItem>,
  pagerState: PagerState
) {
  val coroutineScope = rememberCoroutineScope()
  TabRow(
    selectedTabIndex = pagerState.currentPage,
    backgroundColor = Color.Transparent,
    modifier = Modifier
      .padding(AppTheme.dimensions.regularPadding)
      .background(AppTheme.colors.tabBarBackground, RoundedCornerShape(7.dp))
      .height(28.dp),
    divider = {},
    indicator = { tabPositions ->
      PagerTabRowIndicator(tabPositions = tabPositions, pagerState = pagerState)
    }
  ) {
    pages.forEachIndexed { index, item ->
      Tab(
        modifier = Modifier.zIndex(2f),
        selected = pagerState.currentPage == index,
        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
      ) {
        Text(
          text = item.title,
          style = AppTheme.typography.subtitleRegular,
          color = AppTheme.colors.black
        )
      }
    }
  }
}