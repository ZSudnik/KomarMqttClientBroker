package com.zibi.mod.common.ui.documentCard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import com.zibi.mod.common.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocumentCardCarousel(
  modifier: Modifier = Modifier,
  items: List<DocumentCardItem>,
) {
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  var isForwardDirection by remember { mutableStateOf(false) }

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
      ): Offset {
        val rightToLeft = available.x < 0
        isForwardDirection = rightToLeft
        return Offset.Zero
      }

      override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
      ): Velocity {
        val index = if (isForwardDirection) {
          listState.firstVisibleItemIndex + 1
        } else {
          listState.firstVisibleItemIndex
        }
        coroutineScope.launch { listState.animateScrollToItem(index) }
        return super.onPostFling(
          consumed,
          available
        )
      }
    }
  }
  LazyRow(
    modifier = modifier
      .wrapContentHeight()
      .nestedScroll(nestedScrollConnection),
    userScrollEnabled = true,
    state = listState,
  ) {
    items(items = items) { item ->
      DocumentCard(item = item)
      Spacer(modifier = Modifier.width(AppTheme.dimensions.largePadding))
    }
  }
}
