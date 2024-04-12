package com.zibi.mod.common.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import kotlin.math.sqrt

@Composable
fun ProfileIcon(
  text: String = "",
  showUserIcon: Boolean = false,
  showArrowDownIcon: Boolean = false,
  onClick: () -> Unit
) {
  Row(modifier = Modifier
    .clickable { onClick() }
  ) {
    Box {
      IconButton(
        modifier = Modifier.size(IconSize.Big.dimension),
        interactionSource = NoRippleInteractionSource(),
        onClick = { onClick() },
      ) {
        Box(modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
          .clip(CircleShape)
          .background(AppTheme.colors.primary),
          contentAlignment = Alignment.Center
        ) {
          if (text.isNotEmpty()) {
            Text(
              text = text,
              style = AppTheme.typography.initialsRegular,
              color = Color.White,
              maxLines = 1,
              softWrap = false,
              textAlign = TextAlign.Center,
              modifier = Modifier.align(Alignment.Center),
            )
          } else {
            CustomIcon(
              iconResId = R.drawable.common_ui_ic_human,
              iconSize = IconSize.Small,
              iconColor = Color.White,
              contentDescription = null, // TODO MOB-6712 update contentDescription
            )
          }
        }
      }
      if (showUserIcon) {
        Box(
          modifier = Modifier
            .padding(bottom = 6.dp, end = 8.dp)
            .size(11.dp)
            .clip(CircleShape)
            .background(Color.White)
            .align(Alignment.BottomEnd),
          contentAlignment = Alignment.Center
        ) {
          CustomIcon(
            iconResId = R.drawable.common_ui_ic_user,
            iconSize = IconSize.XSmall,
            iconColor = AppTheme.colors.primary,
            contentDescription = null, // TODO MOB-6712 update contentDescription
          )
        }
      }
    }
    if (showArrowDownIcon) {
      CustomIcon(
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .clickable { onClick() },
        iconResId = R.drawable.common_ui_ic_chevron_down_bold,
        iconSize = IconSize.Small,
        contentDescription = null, // TODO MOB-6712 update contentDescription
      )
    }
  }
}

@Preview
@Composable
fun ProfileIconPreview() {
  ProfileIcon(
    text = "JK",
    onClick = { }
  )
}

@Composable
private fun AutoSizeCenterText(
  text: String,
  size: IconSize,
  showUserIcon: Boolean,
) {
  val sizeInPx = with(LocalDensity.current) { 1.2f * size.dimension.toPx() }
  val mStyle =
    if (showUserIcon) AppTheme.typography.body2Regular else AppTheme.typography.body1Regular
  var textStyle by remember { mutableStateOf(mStyle) }
  var readyToDraw by remember { mutableStateOf(false) }
  Box(
    modifier = Modifier.drawWithContent {
      if (readyToDraw) drawContent()
    },
  ) {
    if (!readyToDraw) {
      Text(
        text = text,
        style = AppTheme.typography.body2Regular,
        color = Color.White,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.Center),
      )
    }
    Text(
      text = text,
      style = textStyle,
      color = Color.White,
      maxLines = 1,
      softWrap = false,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .align(Alignment.Center),
      onTextLayout = { textLayoutResult ->
        val avgGeom = sqrt(textLayoutResult.size.width * textLayoutResult.size.height.toDouble()).toFloat()
        if (avgGeom > 1.15f * sizeInPx) {
          textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.99)
        } else if (avgGeom < 0.85f * sizeInPx) {
          textStyle = textStyle.copy(fontSize = textStyle.fontSize * 1.01)
        } else {
          readyToDraw = true
        }
      }
    )
  }
}