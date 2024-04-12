package com.zibi.mod.common.ui.inputs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.ValidationState
import com.zibi.mod.common.ui.R

@Composable
fun InputDate(
  modifier: Modifier = Modifier,
  value: String = "",
  label: String = stringResource(id = R.string.common_ui_input_field_date_label),
  hint: String = stringResource(id = R.string.common_ui_input_field_date_hint),
  iconResId: Int = R.drawable.common_ui_ic_date_picker,
  validationState: ValidationState = ValidationState.Default,
  onClick: () -> Unit
) {
  val colorBorder = AppTheme.colors.inputFieldBorder
  val colorText = AppTheme.colors.inputFieldText
  val colorLabel = AppTheme.colors.inputFieldLabel
  Column(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight()
  ) {
    Text(
      text = label,
      style = AppTheme.typography.labelRegular,
      color = colorLabel
    )
    Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
    Card(
      modifier = Modifier.clickable(interactionSource = MutableInteractionSource(),
        indication = null,
        enabled = true,
        onClickLabel = null,
        onClick = { onClick() }),
      border = BorderStroke(
        AppTheme.dimensions.xxSmallPadding,
        if (validationState is ValidationState.Invalid) {
          AppTheme.colors.red900
        } else {
          colorBorder
        }
      ),
      backgroundColor = Color.White,
      elevation = AppTheme.dimensions.zero,
    ) {
      Row(
        Modifier
          .background(Color.White)
          .padding(AppTheme.dimensions.regularPadding)
          .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = value.ifEmpty {
            hint
          },
          style = AppTheme.typography.body1Regular,
          color = if (value.isEmpty()) colorText else Color.Black,
        )
        CustomIcon(
          iconResId = iconResId,
          iconSize = IconSize.Medium,
          contentDescription = stringResource(
            id = R.string.common_ui_input_field_date_icon_content_description
          ),
        )
      }
    }
    if (validationState is ValidationState.Invalid) {
      Text(
        text = validationState.message,
        style = AppTheme.typography.labelRegularLight,
        color = AppTheme.colors.red900
      )
    }
  }
}