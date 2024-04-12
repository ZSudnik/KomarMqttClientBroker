package com.zibi.mod.common.ui.toast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.R

@Composable
fun Toast(
  modifier: Modifier = Modifier,
  title: String,
  content: String,
  iconMainResId: Int,
  onCloseIconClick: (() -> Unit)? = null,
  buttonLarge: (@Composable () -> Unit)? = null,
  cardBackgroundColor: Color = AppTheme.colors.secondary,
  iconMainTintColor: Color = AppTheme.colors.primary,
  barBackgroundColor: Color? = null,
  elevation: Dp = AppTheme.dimensions.zero
) {
  val mediumPadding = AppTheme.dimensions.mediumPadding
  val xxMediumPadding = AppTheme.dimensions.xxMediumPadding
  val regularPadding = AppTheme.dimensions.regularPadding
  val xRegularPadding = AppTheme.dimensions.xRegularPadding
  val xLargePadding = AppTheme.dimensions.xLargePadding
  Card(
    modifier = modifier.fillMaxWidth(),
    elevation = elevation,
    shape = RoundedCornerShape(AppTheme.dimensions.cardRadius),
    backgroundColor = cardBackgroundColor
  ) {
    ConstraintLayout {
      val (barRef, iconMainRef, iconCloseRef, titleRef, contentRef, buttonRef) = createRefs()

      if (barBackgroundColor != null) {
        Spacer(modifier = Modifier
          .width(AppTheme.dimensions.smallPadding)
          .background(iconMainTintColor)
          .constrainAs(barRef) {
            start.linkTo(
              parent.start, margin = mediumPadding
            )
            top.linkTo(
              parent.top, margin = xRegularPadding
            )
            bottom.linkTo(contentRef.bottom)
            height = Dimension.fillToConstraints
          })
      }

      IconButton(modifier = Modifier.constrainAs(iconMainRef) {
        top.linkTo(
          parent.top, margin = xRegularPadding
        )
        start.linkTo(
          if (barBackgroundColor == null) parent.start
          else barRef.end, margin = if (barBackgroundColor == null) xRegularPadding
          else regularPadding
        )
      }, enabled = false, onClick = {}, interactionSource = NoRippleInteractionSource()
      ) {
        CustomIcon(
          iconResId = iconMainResId,
          iconSize = IconSize.SBig,
          iconColor = iconMainTintColor,
          contentDescription = null, // TODO MOB-6712 update contentDescription
        )
      }

      IconButton(modifier = Modifier.constrainAs(iconCloseRef) {
        top.linkTo(
          parent.top, margin = xRegularPadding
        )
        end.linkTo(
          parent.end, margin = xRegularPadding
        )
      }, onClick = { onCloseIconClick?.let { it() } }, interactionSource = NoRippleInteractionSource()
      ) {
        CustomIcon(
          modifier = Modifier.size(AppTheme.dimensions.regularPadding),
          iconResId = R.drawable.common_ui_ic_close_bold,
          iconSize = IconSize.Small,
          contentDescription = null, // TODO MOB-6712 update contentDescription
        )
      }

      Text(
        modifier = Modifier.constrainAs(titleRef) {
          start.linkTo(iconMainRef.end, margin = regularPadding)
          end.linkTo(iconCloseRef.start)
          top.linkTo(iconMainRef.top, margin = mediumPadding)
          width = Dimension.fillToConstraints
        }, text = title, style = AppTheme.typography.bodyMedium, overflow = TextOverflow.Ellipsis, maxLines = 1
      )

      Text(
        modifier = Modifier.constrainAs(contentRef) {
          top.linkTo(titleRef.bottom, margin = xxMediumPadding)
          start.linkTo(titleRef.start)
          end.linkTo(parent.end, margin = xRegularPadding)
          bottom.linkTo(
            if (buttonLarge != null) {
              buttonRef.top
            } else {
              parent.bottom
            }, margin = xRegularPadding
          )
          width = Dimension.fillToConstraints
        },
        text = content, style = AppTheme.typography.body2Regular
      )

      Box(
        modifier = Modifier
          .padding(
            start = xLargePadding, end = xLargePadding
          )
          .constrainAs(buttonRef) {
            start.linkTo(parent.start, margin = xLargePadding)
            bottom.linkTo(parent.bottom, margin = xRegularPadding)
            end.linkTo(parent.end, margin = xLargePadding)
          },
      ) {
        if (buttonLarge != null) {
          buttonLarge()
        }
      }
    }
  }
}