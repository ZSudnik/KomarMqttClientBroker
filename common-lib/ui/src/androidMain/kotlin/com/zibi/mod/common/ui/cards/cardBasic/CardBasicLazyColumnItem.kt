package com.zibi.mod.common.ui.cards.cardBasic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun CardBasicLazyColumnItem(
  item: CardBasicItem,
  listSize: Int,
  index: Int
) {
  when {
    listSize == 1 -> CardBasicLazyColumnAloneItem(item)
    index == (listSize - 1) -> CardBasicLazyColumnLastItem(item)
    index == 0 -> CardBasicLazyColumnFirstItem(item)
    else -> CardBasicLazyColumnMidItem(item)
  }
}

@Composable
private fun CardBasicLazyColumnAloneItem(item: CardBasicItem) {
  Row(
    modifier = Modifier.clipToBounds()
  ) {
    Box(
      modifier =
      Modifier
        .padding(
          bottom = AppTheme.dimensions.smallPadding,
          top = AppTheme.dimensions.smallPadding,
          start = AppTheme.dimensions.xRegularPadding,
          end = AppTheme.dimensions.xRegularPadding,
        )
        .fillMaxWidth()
    ) {
      CardBasic(
        item = item,
        showBorder = false,
        shape = RoundedCornerShape(AppTheme.dimensions.cardRadius)
      )
    }
  }
}

@Composable
private fun CardBasicLazyColumnFirstItem(item: CardBasicItem) {
  Row(
    modifier = Modifier.clipToBounds()
  ) {
    Box(
      modifier =
      Modifier
        .padding(
          top = AppTheme.dimensions.smallPadding,
          start = AppTheme.dimensions.xRegularPadding,
          end = AppTheme.dimensions.xRegularPadding,
        )
        .fillMaxWidth()
    ) {
      CardBasic(
        elevation = AppTheme.dimensions.xSmallPadding,
        item = item,
        showBorder = false,
        shape = RoundedCornerShape(
          topStart = AppTheme.dimensions.cardRadius,
          topEnd = AppTheme.dimensions.cardRadius
        )
      )
      Divider(
        modifier = Modifier
          .padding(
            start = AppTheme.dimensions.regularPadding,
            end = AppTheme.dimensions.regularPadding
          )
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
        thickness = AppTheme.dimensions.xxSmallPadding
      )
    }
  }
}

@Composable
private fun CardBasicLazyColumnMidItem(item: CardBasicItem) {
  Row(
    modifier = Modifier.clipToBounds()
  ) {
    Box(
      modifier =
      Modifier
        .padding(
          start = AppTheme.dimensions.xRegularPadding,
          end = AppTheme.dimensions.xRegularPadding,
        )
        .fillMaxWidth()
    ) {
      CardBasic(
        elevation = AppTheme.dimensions.xSmallPadding,
        item = item,
        showBorder = false,
        shape = RoundedCornerShape(AppTheme.dimensions.zero)
      )
      Divider(
        modifier = Modifier
          .padding(
            start = AppTheme.dimensions.regularPadding,
            end = AppTheme.dimensions.regularPadding
          )
          .fillMaxWidth()
          .align(Alignment.BottomCenter),
        thickness = AppTheme.dimensions.xxSmallPadding
      )
    }
  }
}

@Composable
private fun CardBasicLazyColumnLastItem(item: CardBasicItem) {
  Row(
    modifier = Modifier.clipToBounds()
  ) {
    Box(
      modifier =
      Modifier
        .padding(
          bottom = AppTheme.dimensions.smallPadding,
          start = AppTheme.dimensions.xRegularPadding,
          end = AppTheme.dimensions.xRegularPadding,
        )
        .fillMaxWidth()
    ) {
      CardBasic(
        elevation = AppTheme.dimensions.xSmallPadding,
        item = item,
        showBorder = false,
        shape = RoundedCornerShape(
          bottomEnd = AppTheme.dimensions.cardRadius,
          bottomStart = AppTheme.dimensions.cardRadius
        )
      )
    }
  }
}

