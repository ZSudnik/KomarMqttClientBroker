package com.zibi.mod.common.ui.contentBox

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.ValidationState

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ContentBox(
  modifier: Modifier = Modifier,
  padding: Dp = AppTheme.dimensions.xSmallPadding,
  backgroundColor: Color = Color.White,
  shape: Shape = RoundedCornerShape(AppTheme.dimensions.cardRadius),
  elevation: Dp = AppTheme.dimensions.mediumPadding,
  onClick: (() -> Unit)? = null,
  validationState: ValidationState = ValidationState.Default,
  content: @Composable () -> Unit
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding( padding)
      .wrapContentHeight()
      .clickable(interactionSource = MutableInteractionSource(),
        indication = null,
        enabled = onClick != null,
        onClickLabel = null,
        onClick = { onClick?.let { it() } }),
    border = when (validationState) {
      is ValidationState.Invalid -> BorderStroke(
        AppTheme.dimensions.xxSmallPadding,
        AppTheme.colors.red900
      )
      else -> null
    },
    backgroundColor = backgroundColor,
    shape = shape,
    elevation = elevation
  ) {
    content()
  }
  if (validationState is ValidationState.Invalid) {
    Text(
      text = validationState.message,
      style = AppTheme.typography.labelRegularLight,
      color = AppTheme.colors.red900
    )
  }
}