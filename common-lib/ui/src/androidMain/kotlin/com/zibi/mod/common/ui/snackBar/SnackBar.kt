package com.zibi.mod.common.ui.snackBar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.R

@Composable
fun SnackBar(
  modifier: Modifier = Modifier,
  text: String,
  maxLines: Int = 1,
  actionText: String? = null,
  actionIconResId: Int? = null,
  backgroundColor: Color = AppTheme.colors.grey,
  textColor: Color = Color.White,
  actionColor: Color = AppTheme.colors.secondary,
  onActionClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(size = AppTheme.dimensions.cardRadius),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(all = AppTheme.dimensions.xRegularPadding),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = text,
        modifier = Modifier.weight(weight = 1f),
        style = AppTheme.typography.bodyMedium,
        color = textColor,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
      )
      when {
        actionText != null -> Text(
          text = actionText,
          modifier = Modifier.clickable(
            interactionSource = NoRippleInteractionSource(),
            indication = null,
            onClick = onActionClick,
          ),
          style = AppTheme.typography.bodyMedium,
          color = actionColor,
        )

        actionIconResId != null -> CustomIcon(
          modifier = Modifier.clickable(
            interactionSource = NoRippleInteractionSource(),
            indication = null,
            onClick = onActionClick,
          ),
          iconResId = actionIconResId,
          iconSize = IconSize.SMedium,
          iconColor = actionColor,
          contentDescription = null, // TODO MOB-6712 update contentDescription
        )
        else -> Unit
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun SnackBarWithIconPreview() {
  SnackBar(
    text = "Przywrócono połączenie",
    actionIconResId = R.drawable.common_ui_ic_close_bold,
    onActionClick = { }
  )
}

@Preview(showBackground = true)
@Composable
fun SnackBarWithTextPreview() {
  SnackBar(
    text = "Przywrócono połączenie",
    actionText = "Action",
    onActionClick = { }
  )
}
