package com.zibi.mod.common.ui.cards.cardBasic

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.button.small.ButtonSmallWithText
import com.zibi.mod.common.ui.checkbox.RoundedCheckBox
import com.zibi.mod.common.ui.codes.BigImageWithTextDescription
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun CardBasic(
  modifier: Modifier = Modifier,
  item: CardBasicItem,
  elevation: Dp = AppTheme.dimensions.xSmallPadding,
  showBorder: Boolean = true,
  shape: Shape? = null
) {
  val shouldShowBorder: Boolean =
    showBorder && (
      (item as? CardBasicItem.CheckBox)?.isChecked == true ||
        (item as? CardBasicItem.RadioButton)?.isSelected == true
      )
  Card(
    modifier = modifier
      .border(
        width = if (shouldShowBorder) 1.dp else 0.dp,
        color = if (shouldShowBorder) AppTheme.colors.statusBlue1 else Color.Transparent,
        shape = shape ?: RoundedCornerShape(AppTheme.dimensions.cardRadius)
      ),
    backgroundColor = if (item.isEnabled) MaterialTheme.colors.surface else AppTheme.colors.progressBackground,
    shape = shape ?: RoundedCornerShape(AppTheme.dimensions.cardRadius),
    elevation = elevation,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(AppTheme.dimensions.xRegularPadding)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          enabled = item.isEnabled && item !is CardBasicItem.ClickableIcon && item.onClick != null,
          onClickLabel = null,
          onClick = {
            if (item !is CardBasicItem.ClickableIcon && item !is CardBasicItem.ClickableButton) {
              item.onClick?.let { it() }
            }
          }
        ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      when (item) {
        is CardBasicItem.ImageWithSmallButton -> {
          ImageWithSmallButtonSection(item)
        }
        is CardBasicItem.BigImageWithTextDescription -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
          ) {
            Text(
              text = item.title,
              style = AppTheme.typography.body2Regular,
            )
            Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
            BigImageWithTextDescription(
              modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(5f),
              image = item.image,
              imageDescription = item.imageDescription,
            )
          }
        }
        else -> {
          Row(
            modifier = Modifier.weight(3f),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            if (item.iconResId != null) {
              val clickableModifier =
                if (item is CardBasicItem.ClickableIcon) Modifier.clickable { item.onClick?.let { it() } } else Modifier
              CustomIcon(
                modifier = clickableModifier,
                iconResId = item.iconResId,
                iconSize = IconSize.Medium,
              )
              Spacer(modifier = Modifier.width(AppTheme.dimensions.regularPadding))
            }
            Column {
              if (item.info != null) {
                Text(
                  text = item.info,
                  style = AppTheme.typography.body2Regular,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
              }
              Text(
                text = item.title,
                style = AppTheme.typography.bodyMedium,
              )
              if (item.description != null) {
                Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
                Text(
                  text = item.description,
                  style = AppTheme.typography.body2Regular,
                )
              }
            }
          }

          Row(
            modifier = Modifier.weight(if (item is CardBasicItem.ClickableButton) 2f else 1f),
            horizontalArrangement = Arrangement.End,
          ) {
            when (item) {
              is CardBasicItem.Icon -> {
                CustomIcon(
                  iconResId = item.iconForwardResId,
                  iconSize = IconSize.Small,
                )
              }
              is CardBasicItem.ClickableIcon -> {
                CustomIcon(
                  iconResId = item.iconForwardResId,
                  iconColor = item.iconForwardTint,
                  iconSize = IconSize.Small,
                )
              }
              is CardBasicItem.CheckBox -> {
                RoundedCheckBox(
                  isChecked = item.isChecked,
                  isEnabled = item.isEnabled,
                ) { item.onClick?.invoke() }
              }
              is CardBasicItem.SwitchButton -> {
                Switch(
                  checked = item.isChecked,
                  enabled = item.isEnabled,
                  onCheckedChange = { item.onClick?.invoke() },
                )
              }
              is CardBasicItem.RadioButton -> {
                RadioButton(
                  modifier = Modifier.size(20.dp),
                  selected = item.isSelected,
                  onClick = item.onClick,
                  colors = RadioButtonDefaults.colors(
                    selectedColor = AppTheme.colors.primary,
                    unselectedColor = AppTheme.colors.inputFieldBorder
                  ),
                )
              }
              is CardBasicItem.ClickableButton -> {
                ButtonSmallWithText(
                  text = item.buttonText,
                  onClick = { item.onClick?.invoke() }
                )
              }
              is CardBasicItem.Default -> {}
              is CardBasicItem.ImageWithSmallButton -> {}
              else -> {}
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ImageWithSmallButtonSection(item: CardBasicItem.ImageWithSmallButton) {
  Column(
    modifier = Modifier
      .padding(
        top = AppTheme.dimensions.smallPadding,
        bottom = AppTheme.dimensions.smallPadding
      )
  ) {
    Text(
      text = item.title,
      style = AppTheme.typography.body2Regular,
    )
    Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
    Row(
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        bitmap = item.image.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.size(100.dp)
      )
      Spacer(modifier = Modifier.weight(1F))
      ButtonSmallWithText(
        text = item.buttonText,
        onClick = { item.onClick?.invoke() }
      )
    }
  }
}

@Preview
@Composable
fun CardBasicPreview(
  @PreviewParameter(CardBasicItemPreviewParameterProvider::class) cardBasicItem: CardBasicItem
) {
  CardBasic(
    modifier = Modifier,
    item = cardBasicItem,
    elevation = 0.dp,
  )
}
