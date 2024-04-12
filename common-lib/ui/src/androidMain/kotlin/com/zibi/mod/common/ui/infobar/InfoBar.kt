package com.zibi.mod.common.ui.infobar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.R

@Composable
fun InfoBar( //TODO usunąć plik po merge MOB-4544
  modifier: Modifier = Modifier,
  title: String,
  content: String,
  iconMainResId: Int,
  onCloseIconClick: (() -> Unit)? = null,
  buttonLarge: (@Composable () -> Unit)? = null,
  cardBackgroundColor: Color = AppTheme.colors.secondary,
  iconMainTintColor: Color = AppTheme.colors.primary
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    elevation = AppTheme.dimensions.zero,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(cardBackgroundColor)
        .padding(
          start = AppTheme.dimensions.regularPadding,
          top = AppTheme.dimensions.regularPadding,
          end = AppTheme.dimensions.regularPadding,
          bottom = AppTheme.dimensions.largePadding
        ),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier.weight(1f)
        ) {
          IconButton(
            enabled = false,
            onClick = {},
            interactionSource = NoRippleInteractionSource()
          ) {
            CustomIcon(
              iconResId = iconMainResId,
              iconSize = IconSize.Medium,
              iconColor = iconMainTintColor,
              contentDescription = null, // TODO MOB-6712 update contentDescription
            )
          }
        }
        Column(
          modifier = Modifier.weight(7f)
        ) {
          Text(
            modifier = Modifier
              .padding(
                start = AppTheme.dimensions.regularPadding,
                end = AppTheme.dimensions.regularPadding
              )
              .fillMaxWidth(),
            text = title,
            style = AppTheme.typography.bodyMedium
          )
        }
        Column(
          modifier = Modifier.weight(1f)
        ) {
          onCloseIconClick?.let {
            IconButton(
              onClick = { onCloseIconClick() },
              interactionSource = NoRippleInteractionSource()
            ) {
              CustomIcon(
                iconResId = R.drawable.common_ui_ic_close_bold,
                iconSize = IconSize.Small,
                contentDescription = null, // TODO MOB-6712 update contentDescription
              )
            }
          }
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {

        }
        Column(modifier = Modifier.weight(8f)) {
          Text(
            modifier = Modifier
              .padding(
                start = AppTheme.dimensions.regularPadding,
                end = AppTheme.dimensions.regularPadding
              )
              .fillMaxWidth(),
            text = content,
            style = AppTheme.typography.body2Regular
          )
        }
      }
      buttonLarge?.let {
        Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          buttonLarge()
        }
      }
    }
  }
}
