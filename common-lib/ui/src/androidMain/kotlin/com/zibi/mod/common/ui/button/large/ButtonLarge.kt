package com.zibi.mod.common.ui.button.large

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun ButtonLarge(
  modifier: Modifier = Modifier,
  text: String,
  style: ButtonLargeStyle = ButtonLargeStyle.PRIMARY,
  iconResId: Int? = null,
  disable: Boolean = false,
  textColor: Color? = null,
  backgroundColor: Color? = null,
  borderColor: Color? = null,
  iconTextPadding: Dp? = null,
  elevation: Dp = AppTheme.dimensions.zero,
  shape: RoundedCornerShape = RoundedCornerShape(50),
  onClick: () -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val rippleInteractionSource = remember { NoRippleInteractionSource() }
  val textColorValue = textColor ?: when {
    disable -> AppTheme.colors.inputFieldText
    style == ButtonLargeStyle.PRIMARY -> Color.White
    else -> AppTheme.colors.primary
  }
  val backgroundColorValue = backgroundColor ?: when {
    disable -> AppTheme.colors.inputFieldBorder
    style == ButtonLargeStyle.PRIMARY -> AppTheme.colors.primary
    else -> Color.Transparent
  }
  val borderColorValue = borderColor ?: when {
    disable -> AppTheme.colors.inputFieldBorder
    style == ButtonLargeStyle.PRIMARY -> AppTheme.colors.primary
    style == ButtonLargeStyle.SECONDARY -> AppTheme.colors.primary
    style == ButtonLargeStyle.TERTIARY -> Color.Transparent
    else -> backgroundColorValue
  }
  when (style) {
    ButtonLargeStyle.PRIMARY -> {
      Button(
        onClick = {
          focusManager.clearFocus(force = true)
          onClick()
        },
        modifier = modifier.fillMaxWidth(),
        interactionSource = rippleInteractionSource,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
          backgroundColor = backgroundColorValue,
          contentColor = Color.White
        ),
        border = BorderStroke(
          width = if (disable) AppTheme.dimensions.zero
          else AppTheme.dimensions.xxSmallPadding,
          color = borderColorValue
        ),
        enabled = !disable,
        elevation = ButtonDefaults.elevation(elevation)
      ) {
        if (iconResId != null) {
          CustomIcon(
            iconResId = iconResId,
            iconColor = textColorValue,
            iconSize = IconSize.Medium,
          )
          Spacer(modifier = Modifier.width(iconTextPadding ?: AppTheme.dimensions.regularPadding))
        }
        Text(
          modifier = Modifier.padding(
            top = AppTheme.dimensions.mediumPadding,
            bottom = AppTheme.dimensions.mediumPadding
          ),
          text = text,
          color = textColorValue,
          style = AppTheme.typography.bodyMedium
        )
      }
    }
    ButtonLargeStyle.SECONDARY -> {
      OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        interactionSource = rippleInteractionSource,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
          backgroundColor = backgroundColorValue
        ),
        border = BorderStroke(
          width = if (disable) AppTheme.dimensions.zero
          else AppTheme.dimensions.xxSmallPadding,
          color = borderColorValue,
        ),
        enabled = !disable,
        onClick = {
          focusManager.clearFocus(force = true)
          onClick()
        },
        elevation = ButtonDefaults.elevation(elevation)
      ) {
        if (iconResId != null) {
          CustomIcon(
            iconResId = iconResId,
            iconColor = textColorValue,
            iconSize = IconSize.Medium,
          )
          Spacer(modifier = Modifier.width(iconTextPadding ?: AppTheme.dimensions.mediumPadding))
        }
        Text(
          modifier = Modifier.padding(
            top = AppTheme.dimensions.mediumPadding,
            bottom = AppTheme.dimensions.mediumPadding
          ),
          text = text,
          color = textColorValue,
          style = AppTheme.typography.bodyMedium
        )
      }
    }
    ButtonLargeStyle.TERTIARY -> {
      TextButton(
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = backgroundColorValue
        ),
        border = BorderStroke(
          width = if (disable) AppTheme.dimensions.zero
          else AppTheme.dimensions.xxSmallPadding,
          color = borderColorValue,
        ),
        enabled = !disable,
        interactionSource = rippleInteractionSource,
        onClick = {
          focusManager.clearFocus(force = true)
          onClick()
        },
        shape = shape,
        elevation = ButtonDefaults.elevation(elevation)
      ) {
        if (iconResId != null) {
          CustomIcon(
            iconResId = iconResId,
            iconColor = textColorValue,
            iconSize = IconSize.Medium,
          )
          Spacer(modifier = Modifier.width(iconTextPadding ?: AppTheme.dimensions.regularPadding))
        }
        Text(
          modifier = Modifier.padding(
            top = AppTheme.dimensions.mediumPadding,
            bottom = AppTheme.dimensions.mediumPadding
          ),
          text = text,
          color = textColorValue,
          style = AppTheme.typography.bodyMedium
        )
      }
    }
  }
}

@Preview
@Composable
fun ButtonLargePreview() {
  Column {
    ButtonLarge(text = "Lorem ipsum",
      onClick = {})
    ButtonLarge(text = "Lorem ipsum",
      style = ButtonLargeStyle.SECONDARY,
      borderColor = AppTheme.colors.inputFieldBorder,
      onClick = {})
    ButtonLarge(text = "Lorem ipsum",
      style = ButtonLargeStyle.TERTIARY,
      borderColor = AppTheme.colors.inputFieldBorder,
      onClick = {})
  }
}