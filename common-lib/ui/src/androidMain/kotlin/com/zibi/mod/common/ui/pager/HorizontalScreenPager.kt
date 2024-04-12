package com.zibi.mod.common.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@OptIn( ExperimentalFoundationApi::class)
@Composable
fun HorizontalScreenPager(
  modifier: Modifier = Modifier,
  pages: List<TabRowItem>,
  onPagerPageChanged: () -> Int
) {
  val pagerState = rememberPagerState(initialPage = 0, pageCount = onPagerPageChanged)
  LaunchedEffect(pagerState.currentPage) {
    onPagerPageChanged()  // pagerState.currentPage
  }
  Column(
    modifier = modifier
  ) {
    PagerTabRow(pages = pages, pagerState = pagerState)
    HorizontalPager( state = pagerState) { page ->
      pages[page].screen()
    }
  }
}