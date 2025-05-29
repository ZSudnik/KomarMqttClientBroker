package com.zibi.mod.common.ui.dropDown

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.utils.ValidationState
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropDown(
  modifier: Modifier = Modifier,
  label: String,
  hint: String,
  value: String?,
  validationState: ValidationState = ValidationState.Default,
  onClick: () -> Unit,
) {
  val bringIntoViewRequester = remember { BringIntoViewRequester() }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .bringIntoViewRequester(bringIntoViewRequester)
  ) {
    Text(
      text = label,
      style = AppTheme.typography.labelRegular,
      color = AppTheme.colors.inputFieldLabel
    )
    Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
    Card(
      modifier = modifier,
      elevation = AppTheme.dimensions.zero,
      border = BorderStroke(
        width = AppTheme.dimensions.xxSmallPadding,
        color = if (validationState is ValidationState.Invalid) {
          LaunchedEffect(Unit) {
            bringIntoViewRequester.bringIntoView()
          }
          AppTheme.colors.red900
        } else {
          AppTheme.colors.inputFieldBorder
        }
      ),
      shape = RoundedCornerShape(AppTheme.dimensions.smallPadding),
      backgroundColor = Color.White
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(AppTheme.dimensions.smallPadding)
          .clickable(interactionSource = MutableInteractionSource(),
            indication = null,
            onClickLabel = null,
            onClick = { onClick() }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          modifier = Modifier.padding(start = AppTheme.dimensions.xxMediumPadding),
          text = if (value.isNullOrEmpty()) hint else value,
          style = if (value.isNullOrEmpty()) AppTheme.typography.body2Regular else AppTheme.typography.body1Regular,
          color = if (value.isNullOrEmpty()) AppTheme.colors.inputFieldText else Color.Unspecified,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (validationState is ValidationState.Invalid) {
            CustomIcon(
              modifier = Modifier.padding(start = AppTheme.dimensions.mediumPadding),
              iconResId = R.drawable.common_ui_ic_erorr_mark,
              iconColor = AppTheme.colors.red900,
              iconSize = IconSize.Medium,
              contentDescription = "Ikona błędu", // TODO MOB-6712 update contentDescription
            )
          }
          IconButton(
            enabled = true,
            onClick = { onClick() },
            interactionSource = NoRippleInteractionSource()
          ) {
            CustomIcon(
              iconResId = R.drawable.common_ui_ic_chevron_down_bold,
              iconSize = IconSize.Small,
              iconColor = if (validationState is ValidationState.Invalid) {
                AppTheme.colors.red900
              } else {
                LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
              },
              contentDescription = null, // TODO MOB-6712 update contentDescription
            )
          }
        }
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