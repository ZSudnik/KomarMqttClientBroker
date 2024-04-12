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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.utils.dpToPx
import java.lang.Math.PI
import java.lang.Math.min
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SwitchGradientComponent(
  modifier: Modifier = Modifier,
  checked: Boolean,
  enabled: Boolean = true,
  width: Dp = AppTheme.dimensions.switchComponentWidth,
  height: Dp = AppTheme.dimensions.switchComponentHeight,
  checkedThumbColor: List<Color>,
  uncheckedThumbColor: List<Color>,
  checkedTrackColor: Color = Color.LightGray,
  uncheckedTrackColor: Color = Color.LightGray,
  onCheckedChange: (Boolean) -> Unit,
) {
  Box(
    modifier = modifier
      .background(
        color = if(checked) checkedTrackColor else uncheckedTrackColor,
        shape = RoundedCornerShape(percent = 100)
      )
      .width(width)
      .height(height)
      .clickable(
        interactionSource = NoRippleInteractionSource(),
        indication = null,
        onClick = {
         if(enabled) onCheckedChange(!checked)
        }
      )
  ) {
    Box(
      modifier = Modifier
        .size(height)
        .padding(1.dp)
        .background(
          brush = if( checked)
            Brush.radialGradient(
              colors =  checkedThumbColor ,
              center = Offset(height.dpToPx()/2, height.dpToPx() / 2)
            )
          else
            Brush.sweepGradient(
              colors =  uncheckedThumbColor,
              center = Offset(height.dpToPx()/2, height.dpToPx() / 2)
            ),
          RoundedCornerShape(percent = 100)
        )
        .align(
          if (checked) Alignment.CenterEnd else Alignment.CenterStart
        )
    )
  }
}
