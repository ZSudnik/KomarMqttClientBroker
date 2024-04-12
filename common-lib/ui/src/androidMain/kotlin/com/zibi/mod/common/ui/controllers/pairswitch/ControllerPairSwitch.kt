package com.zibi.mod.common.ui.controllers.pairswitch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun ControllerPairSwitch(
  leftItem: ControllerPairSwitchItem,
  rightItem: ControllerPairSwitchItem,
  selectedItemType: ControllerPairSwitchType = ControllerPairSwitchType.LEFT,
  boxBackgroundColor: Color = AppTheme.colors.controllerSwitchBackground,
  textColor: Color = AppTheme.colors.grey,
  selectedTextColor: Color = AppTheme.colors.primary,
  onSelectItemChanged: (ControllerPairSwitchType) -> Unit
) {
  val isSelectedItem: (ControllerPairSwitchType) -> Boolean = {
    it == selectedItemType
  }
  Row(
    modifier = Modifier
      .background(
        boxBackgroundColor,
        RoundedCornerShape(50)
      )
      .height(AppTheme.dimensions.controllerSwitchBoxHeight)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    listOf(
      ControllerPairSwitchType.LEFT to leftItem,
      ControllerPairSwitchType.RIGHT to rightItem,
    ).forEach { (type, item) ->
      val buttonElevationValue = if (isSelectedItem(type)) AppTheme.dimensions.smallPadding
      else AppTheme.dimensions.zero
      Button(
        modifier = Modifier
          .padding(
            start = AppTheme.dimensions.smallPadding,
            end = AppTheme.dimensions.smallPadding
          )
          .height(AppTheme.dimensions.controllerButtonHeight)
          .weight(1f),
        shape = RoundedCornerShape(AppTheme.dimensions.xLargePadding),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = if (isSelectedItem(type)) Color.White else boxBackgroundColor
        ),
        elevation = ButtonDefaults.elevation(
          defaultElevation = buttonElevationValue,
          pressedElevation = buttonElevationValue,
        ),
        interactionSource = NoRippleInteractionSource(),
        onClick = {
          onSelectItemChanged(type)
        },
      ) {
        if (item.iconResId != null) {
          CustomIcon(
            iconResId = item.iconResId,
            iconSize = IconSize.Small,
            iconColor = if (isSelectedItem(type)) selectedTextColor else textColor,
            contentDescription = null, // TODO MOB-6712 update contentDescription
          )
          Spacer(modifier = Modifier.width(AppTheme.dimensions.mediumPadding))
        }
        Text(
          text = item.text ?: String(),
          style = AppTheme.typography.bodyMedium,
          color = if (isSelectedItem(type)) selectedTextColor else textColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
