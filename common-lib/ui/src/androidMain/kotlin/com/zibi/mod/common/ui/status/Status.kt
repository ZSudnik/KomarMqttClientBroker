package com.zibi.mod.common.ui.status

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

enum class StatusType {
  VALID_STATUS,
  WARNING_STATUS,
  ERROR_STATUS,
  INACTIVE_STATUS,
  INFO_STATUS,
  VALID_STATUS_ALTERNATIVE,
  WARNING_STATUS_ALTERNATIVE,
  ERROR_STATUS_ALTERNATIVE,
  INFO_STATUS_ALTERNATIVE;

  val backgroundColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
      VALID_STATUS_ALTERNATIVE -> AppTheme.colors.validStatusBackground
      WARNING_STATUS_ALTERNATIVE -> AppTheme.colors.warningStatusBackground
      ERROR_STATUS_ALTERNATIVE -> AppTheme.colors.errorStatusBackground
      INFO_STATUS_ALTERNATIVE -> AppTheme.colors.infoStatusBackground
      else -> Color.White
    }

  val borderColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
      VALID_STATUS_ALTERNATIVE -> AppTheme.colors.validStatusBorder
      WARNING_STATUS_ALTERNATIVE -> AppTheme.colors.warningStatusYellowBorder
      ERROR_STATUS_ALTERNATIVE -> AppTheme.colors.errorStatusBorder
      INFO_STATUS_ALTERNATIVE -> AppTheme.colors.infoStatusBorder
      else -> AppTheme.colors.inputFieldBorder
    }

  val statusColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
      VALID_STATUS, VALID_STATUS_ALTERNATIVE -> AppTheme.colors.green900
      WARNING_STATUS, WARNING_STATUS_ALTERNATIVE -> AppTheme.colors.yellow900
      ERROR_STATUS, ERROR_STATUS_ALTERNATIVE -> AppTheme.colors.red900
      INACTIVE_STATUS -> AppTheme.colors.grey
      INFO_STATUS, INFO_STATUS_ALTERNATIVE -> AppTheme.colors.blue900
    }

  val textColor: Color
    @Composable @ReadOnlyComposable get() = when (this) {
      VALID_STATUS_ALTERNATIVE, WARNING_STATUS_ALTERNATIVE, ERROR_STATUS_ALTERNATIVE, INFO_STATUS_ALTERNATIVE ->
        AppTheme.colors.black
      else -> AppTheme.colors.grey
    }
}

@Composable
fun Status(
  text: String,
  type: StatusType,
  iconResId: Int? = null,
  onClick: (() -> Unit)? = null
) {
  OutlinedButton(
    enabled = false,
    onClick = { onClick?.let { it() } },
    interactionSource = NoRippleInteractionSource(),
    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = type.backgroundColor),
    border = BorderStroke(
      width = ButtonDefaults.outlinedBorder.width,
      color = type.borderColor
    ),
    shape = RoundedCornerShape(50),
    contentPadding = PaddingValues(horizontal = AppTheme.dimensions.mediumPadding),
  ) {
    Column(
      modifier = Modifier.wrapContentSize(Alignment.Center)
    ) {
      if (iconResId != null) {
        CustomIcon(
          iconResId = iconResId,
          iconSize = IconSize.Small,
          iconColor = type.statusColor,
          contentDescription = null, // TODO MOB-6712 update contentDescription
        )
      } else {
        Box(
          modifier = Modifier
            .size(AppTheme.dimensions.statusWithoutIconSize)
            .clip(CircleShape)
            .background(type.statusColor)
        )
      }
    }
    Spacer(
      modifier = Modifier.size(AppTheme.dimensions.smallPadding)
    )
    Text(
      text = text,
      style = AppTheme.typography.label2Regular,
      color = type.textColor
    )
  }
}

@Preview
@Composable
fun StatusPreview() {
  Status(
    text = "Status",
    type = StatusType.WARNING_STATUS
  )
}