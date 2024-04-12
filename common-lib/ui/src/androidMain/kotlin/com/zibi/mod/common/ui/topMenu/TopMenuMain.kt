package com.zibi.mod.common.ui.topMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.profile.ProfileIcon
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.common.ui.R

@Composable
fun TopMenuMain(
  mainIconText: String? = null,
  showUserIcon: Boolean = false,
  showArrowDownIcon: Boolean = false,
  showRedDotOnNotification: Boolean = false,
  onMainIconClick: () -> Unit,
  onNotificationIconClick: () -> Unit,
  backgroundColor: Color = AppTheme.colors.background,
  modifier: Modifier = Modifier,
) {
  TopAppBar(
    modifier = modifier,
    title = {},
    backgroundColor = backgroundColor,
    navigationIcon = {
      ProfileIcon(text = mainIconText ?: String(),
        showUserIcon = showUserIcon,
        showArrowDownIcon = showArrowDownIcon,
        onClick = { onMainIconClick() })
    },
    actions = {
      IconButton(interactionSource = NoRippleInteractionSource(), onClick = { onNotificationIconClick() }) {
        BadgedBox(badge = {
          if (showRedDotOnNotification) {
            Box(
              modifier = Modifier
                .size(13.dp)
                .absoluteOffset(
                  x = -AppTheme.dimensions.smallPadding, y = AppTheme.dimensions.smallPadding
                )
                .clip(CircleShape)
                .background(AppTheme.colors.background), contentAlignment = Alignment.Center
            ) {
              Badge(
                backgroundColor = AppTheme.colors.statusRedWeb, modifier = Modifier.padding(2.dp)
              )
            }
          }
        }) {
          CustomIcon(
            iconResId = R.drawable.common_ui_ic_notifications,
            iconColor = AppTheme.colors.black,
            iconSize = IconSize.Medium,
          )
        }
      }
    },
    elevation = AppTheme.dimensions.zero
  )
}

@Preview
@Composable
fun TopMenuMainPreview() {
  TopMenuMain(mainIconText = "IL", onMainIconClick = { }, onNotificationIconClick = { })
}