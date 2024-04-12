package com.zibi.mod.common.ui.tooltips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.R

@Composable
fun BottomSheetTooltip(
  tooltipTitle: String,
  tooltipContent: String,
  onDismissRequest: () -> Unit
) {

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(color = Color.White)
      .padding(all = AppTheme.dimensions.bottomTooltipPadding)
      .verticalScroll(rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {

      Text(
        text = tooltipTitle,
        style = AppTheme.typography.subtitle
      )

      IconButton(
        modifier = Modifier.size(15.dp),
        enabled = true,
        onClick = onDismissRequest,
        interactionSource = NoRippleInteractionSource()
      ) {
        CustomIcon(
          iconResId = R.drawable.common_ui_ic_close_bold,
          iconSize = IconSize.Small,
          contentDescription = "Close tooltip", // TODO MOB-6712 update contentDescription
        )
      }

    }
    Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))

    Text(
      text = tooltipContent,
      style = AppTheme.typography.body1Regular,
      textAlign = TextAlign.Start
    )
    Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))

    ButtonLarge(
      text = "Zamknij",
      onClick = onDismissRequest
    )
  }
}