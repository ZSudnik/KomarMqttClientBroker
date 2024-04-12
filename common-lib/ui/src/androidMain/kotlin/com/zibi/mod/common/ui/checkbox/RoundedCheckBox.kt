package com.zibi.mod.common.ui.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun RoundedCheckBox(
  isChecked: Boolean,
  modifier: Modifier = Modifier,
  checkedColor: Color = AppTheme.colors.primary,
  isEnabled: Boolean = true,
  onChecked: (Boolean) -> Unit = {},
) {
  Box(contentAlignment = Alignment.Center,
    modifier = modifier
      .size(20.dp)
      .clickable(enabled = isEnabled) { onChecked(isChecked.not()) }
      .border(
        width = if (isChecked) 0.dp else 2.dp,
        color = when (isEnabled) {
          true -> if (isChecked) Color.Transparent else AppTheme.colors.inputFieldBorder
          false -> AppTheme.colors.inputFieldText
        },
        shape = RoundedCornerShape(4.dp)
      )
      .background(
        color = if (isChecked) checkedColor else Color.Transparent,
        shape = RoundedCornerShape(4.dp)
      )) {
    if (isChecked) {
      CustomIcon(
        iconResId = R.drawable.common_ui_ic_check_mark,
        iconSize = IconSize.Small,
        iconColor = Color.White,
        contentDescription = null, // TODO MOB-6712 update contentDescription
      )
    }
  }
}
