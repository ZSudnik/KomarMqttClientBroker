package com.zibi.mod.common.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class IconSize(val dimension: Dp) {
  XSmall(6.dp),
  MSmall(14.dp),
  Small(16.dp),
  SMedium(20.dp),
  Medium(24.dp),
  SBig(32.dp),
  Big(48.dp),
  XBig(70.dp),
  XXBig(96.dp),
}

enum class IconBackgroundShape {
  Circle, Square, None
}

@Composable
fun CustomIcon(
  modifier: Modifier = Modifier,
  @DrawableRes iconResId: Int? = null,
  iconSize: IconSize = IconSize.Medium,
  iconColor: Color = Color.Unspecified,
  iconOpacity: Float = 1f,
  iconBackgroundShape: IconBackgroundShape = IconBackgroundShape.None,
  iconBackgroundSize: IconSize = IconSize.Big,
  iconBackgroundColor: Color = Color.Unspecified,
  iconBackgroundOpacity: Float = 1f,
  iconBackgroundCornerRadius: Float = 0f,
  contentDescription: String? = null,
) {
  Box(modifier = modifier,
    contentAlignment = Alignment.Center) {
    if (iconBackgroundShape != IconBackgroundShape.None) {
      Canvas(modifier = Modifier.size(iconBackgroundSize.dimension),
        onDraw = {
          when (iconBackgroundShape) {
            IconBackgroundShape.Circle ->
              drawCircle(
                color = iconBackgroundColor,
                alpha = iconBackgroundOpacity
              )
            IconBackgroundShape.Square ->
              drawRoundRect(
                color = iconBackgroundColor,
                cornerRadius = CornerRadius(iconBackgroundCornerRadius),
                alpha = iconBackgroundOpacity
              )
            else -> {}
          }
        })
    }
    iconResId?.let {
      Icon(
        modifier = Modifier
          .size(iconSize.dimension)
          .alpha(iconOpacity),
        painter = painterResource(id = iconResId),
        contentDescription = contentDescription,
        tint = iconColor
      )
    }
  }
}
