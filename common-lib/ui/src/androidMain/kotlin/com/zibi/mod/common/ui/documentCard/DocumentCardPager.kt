package com.zibi.mod.common.ui.documentCard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme

@ExperimentalFoundationApi
@Composable
fun DocumentCardPager(
    modifier: Modifier = Modifier,
    items: List<DocumentCardItem>,
) {
    val pagerState = rememberPagerState(
        initialPage = 1,
        initialPageOffsetFraction = 0f,
        pageCount = { items.size }
    )
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val horizontalContentPadding = when {
        screenWidth > AppTheme.dimensions.documentCardWidth.value ->
            ((screenWidth - AppTheme.dimensions.documentCardWidth.value) / 2).dp

        else -> AppTheme.dimensions.mediumPadding
    }

    HorizontalPager(
      modifier = modifier,
      state = pagerState,
      pageSpacing = AppTheme.dimensions.mediumPadding,
      userScrollEnabled = true,
      reverseLayout = false,
      contentPadding = PaddingValues(
        horizontal = horizontalContentPadding,
        vertical = AppTheme.dimensions.regularPadding
      ),
      outOfBoundsPageCount = 0,
      pageSize = PageSize.Fill,
      flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
      key = null,
      pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
          state = pagerState,
          orientation = Orientation.Horizontal
      ),
      pageContent =
        DocumentCard(
          item = items[ pagerState.currentPage],
        )
    )
}