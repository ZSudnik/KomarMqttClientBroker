package com.zibi.mod.common.ui.switchcomponent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.SwitchColors
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.utils.ValidationState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwitchComponentWithText(
  initialCheckedState: Boolean = false,
  description: String? = null,
  enabled: Boolean = true,
  width: Dp = AppTheme.dimensions.switchComponentWidth,
  height: Dp = AppTheme.dimensions.switchComponentHeight,
  validationState: ValidationState = ValidationState.Default,
  colors: SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    uncheckedThumbColor = Color.White,
    checkedTrackColor = AppTheme.colors.statusBlue1,
    checkedTrackAlpha = 1F,
    uncheckedTrackColor = AppTheme.colors.switchComponentUnchecked,
  ),
  onCheckedChange: (Boolean) -> Unit = {},
  onLinkClick: () -> Unit = {},
) {
  var checked by remember { mutableStateOf(initialCheckedState) }
  val bringIntoViewRequester = remember { BringIntoViewRequester() }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .bringIntoViewRequester(bringIntoViewRequester)
      .clickable(
        interactionSource = NoRippleInteractionSource(),
        indication = null,
        onClick = {
          //14.04.2023: TODO: MOB-6799 Move to link click when hyperlinks parsing is done
          onLinkClick()
        }
      )
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      SwitchComponent(
        checked = checked,
        enabled = enabled,
        width = width,
        height = height,
        colors = colors,
        onCheckedChange = {
          checked = checked.not()
          onCheckedChange(checked)
        },
      )
      if (description != null) {
        Spacer(modifier = Modifier.width(AppTheme.dimensions.mediumPadding))
        Text(
          text = description,
          style = AppTheme.typography.body2RegularLight,
          color = AppTheme.colors.black,
        )
      }
    }

    if (validationState is ValidationState.Invalid) {
      Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
      Text(
        text = validationState.message,
        style = AppTheme.typography.labelRegularLight,
        color = AppTheme.colors.red900
      )
    }
  }
}