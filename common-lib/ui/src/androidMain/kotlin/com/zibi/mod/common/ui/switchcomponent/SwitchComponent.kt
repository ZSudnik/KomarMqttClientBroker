package com.zibi.mod.common.ui.switchcomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun SwitchComponent(
  modifier: Modifier = Modifier,
  checked: Boolean,
  enabled: Boolean = true,
  width: Dp = AppTheme.dimensions.switchComponentWidth,
  height: Dp = AppTheme.dimensions.switchComponentHeight,
  colors: SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    uncheckedThumbColor = Color.White,
    checkedTrackColor = AppTheme.colors.statusBlue1,
    checkedTrackAlpha = 1F,
    uncheckedTrackColor = AppTheme.colors.switchComponentUnchecked,
  ),
  onCheckedChange: (Boolean) -> Unit,
) {
  Box(
    modifier = modifier
      .background(
        color = colors.trackColor(enabled = enabled, checked = checked).value,
        shape = RoundedCornerShape(percent = 100)
      )
      .width(width)
      .height(height)
      .clickable(
        interactionSource = NoRippleInteractionSource(),
        indication = null,
        onClick = {
          onCheckedChange(!checked)
        }
      )
  ) {
    Box(
      modifier = Modifier
        .size(height)
        .padding(1.dp)
        .background(
          color = colors.thumbColor(enabled = enabled, checked = checked).value,
          RoundedCornerShape(percent = 100)
        )
        .align(
          if (checked) Alignment.CenterEnd else Alignment.CenterStart
        )
    )
  }
}