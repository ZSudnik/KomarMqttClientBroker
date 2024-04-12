package com.zibi.mod.common.ui.cards.cardBasic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun CardBasicList(
  modifier: Modifier = Modifier,
  items: List<CardBasicItem>,
  elevation: Dp = AppTheme.dimensions.xSmallPadding,
  animateSizeChange: Boolean = false,
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(AppTheme.dimensions.cardRadius),
    elevation = elevation,
  ) {
    Column(
      modifier = if (animateSizeChange) {
        Modifier.animateContentSize(
          animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
          )
        )
      } else {
        Modifier
      }
    ) {
      items.forEachIndexed { index, item ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          CardBasic(
            elevation = AppTheme.dimensions.zero,
            item = item,
            showBorder = false
          )
        }
        if (index < (items.size - 1)) {
          Row {
            Divider(
              modifier = Modifier.padding(
                start = AppTheme.dimensions.regularPadding,
                end = AppTheme.dimensions.regularPadding
              ),
              thickness = AppTheme.dimensions.xxSmallPadding
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun CardBasicListPreview(
  @PreviewParameter(CardBasicItemPreviewParameterProvider::class) cardBasicItem: CardBasicItem
) {
  CardBasicList(
    items = listOf(
      cardBasicItem,
      cardBasicItem,
      cardBasicItem
    ),
    elevation = 0.dp,
  )
}