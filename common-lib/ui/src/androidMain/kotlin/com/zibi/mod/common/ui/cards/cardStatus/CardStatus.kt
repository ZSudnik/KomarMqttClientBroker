package com.zibi.mod.common.ui.cards.cardStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.zibi.mod.common.ui.button.ButtonData
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.contentBox.ContentBox
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.status.Status
import com.zibi.mod.common.ui.status.StatusType
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun CardStatus(
  modifier: Modifier = Modifier,
  item: CardStatusItem,
) {
  var size by remember { mutableStateOf(Size.Zero) }
  ContentBox(
    modifier = modifier,
    onClick = item.onItemClick,
  ) {
    Column(
      modifier = Modifier.padding(AppTheme.dimensions.xRegularPadding)
    ) {
      Row() {
        item.barBackgroundColor?.let {
          Column(
            modifier = Modifier.wrapContentWidth()
          ) {
            Spacer(
              modifier = Modifier
                .height(size.height.dp / 3)
                .width(AppTheme.dimensions.smallPadding)
                .background(it)
            )
          }
        }
        Box {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .onGloballyPositioned { coordinates ->
                size = coordinates.size.toSize()
              }
              .padding(
                start = if (item.barBackgroundColor != null) {
                  AppTheme.dimensions.regularPadding
                } else {
                  AppTheme.dimensions.zero
                }
              )
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              item.status?.let {
                Status(
                  text = it.text,
                  type = it.type,
                  iconResId = it.iconResId,
                )
              }
              item.additionalInfo?.let {
                Text(
                  text = it,
                  style = AppTheme.typography.label2Regular,
                  color = AppTheme.colors.grey,
                )
              }
            }
            Spacer(modifier = Modifier.height(AppTheme.dimensions.xxMediumPadding))
            Text(
              text = item.title,
              style = AppTheme.typography.bodyMedium,
              color = AppTheme.colors.black,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
            item.body?.let {
              Text(
                text = it,
                style = AppTheme.typography.body2Regular
              )
              Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
            }
            val itemAmount = item.amount
            val itemAmountTitle = item.amountTitle
            if (itemAmount != null && itemAmountTitle != null) {
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = itemAmountTitle,
                  style = AppTheme.typography.body2Regular,
                  color = AppTheme.colors.grey,
                )
                Spacer(modifier = Modifier.width(AppTheme.dimensions.smallPadding))
                Text(
                  text = StringBuilder().append(itemAmount).append(" ").append(item.currency).toString(),
                  style = AppTheme.typography.bodyMedium,
                  color = AppTheme.colors.grey,
                )
              }
              Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))
            }
          }
          if (item.iconForwardResId != null) {
            CustomIcon(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = AppTheme.dimensions.regularPadding),
              iconResId = item.iconForwardResId,
              iconSize = IconSize.Small,
            )
          }
        }
      }
      Row() {
        Column() {
          item.primaryButtonData?.let {
            ButtonLarge(
              text = it.text,
              onClick = it.onClick,
            )
          }
          if (item.primaryButtonData != null && item.secondaryButtonData != null) {
            Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
          }
          item.secondaryButtonData?.let {
            ButtonLarge(
              text = it.text,
              style = ButtonLargeStyle.SECONDARY,
              onClick = it.onClick,
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
fun CardStatusPreview() {
  CardStatus(
    item = CardStatusItem(
      title = "Opłata za przekształcenie gruntów Gminy Lublin",
      amount = "366,00",
      amountTitle = "Zapłać:",
      currency = "zł",
      status = StatusData(
        text = "Zrealizowano",
        type = StatusType.ERROR_STATUS
      ),
      additionalInfo = "Rata 1 z 3",
      primaryButtonData = ButtonData(
        text = "Zobacz szczegóły",
        onClick = {}
      ),
      onItemClick = {},
    )
  )
}
