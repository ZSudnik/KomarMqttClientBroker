package com.zibi.mod.common.ui.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ripple
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun BottomNavigation(
  items: List<BottomNavigationItem>,
  modifier: Modifier = Modifier,
  selectedItemIndex: Int = 0,
  boldIfSelected: Boolean = false,
) {
  BottomNavigationContainer(modifier) {
    items.forEachIndexed { index, item ->
      BottomNavigationItem(
        selected = selectedItemIndex == index,
        iconResId = item.iconResId,
        label = item.label,
        boldIfSelected = boldIfSelected,
        onClick = { item.onClickAction.invoke() })
    }
  }
}

@Composable
private fun BottomNavigationContainer(
  modifier: Modifier = Modifier,
  backgroundColor: Color = AppTheme.colors.background,
  content: @Composable RowScope.() -> Unit
) {
  Surface(
    color = backgroundColor,
    contentColor = backgroundColor,
    modifier = modifier
  ) {
    Row(
      Modifier
        .fillMaxWidth()
        .padding(vertical = 9.dp),
      content = content,
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
    )
  }
}

@Composable
private fun RowScope.BottomNavigationItem(
  selected: Boolean,
  onClick: () -> Unit,
  @DrawableRes iconResId: Int,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  label: String? = null,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  selectedContentColor: Color = AppTheme.colors.statusBlue1,
  unselectedContentColor: Color = AppTheme.colors.inputFieldLabel,
  selectedIconBackground: Color = AppTheme.colors.secondary,
  unselectedIconBackground: Color = Color(0x00000000),
  boldIfSelected: Boolean = false
) {
  val ripple = ripple(
    bounded = false,
    color = selectedContentColor
  )

  Box(
    modifier
      .selectable(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        role = Role.Tab,
        interactionSource = interactionSource,
        indication = ripple
      )
      .weight(1f),
    contentAlignment = Alignment.Center
  ) {
    BottomNavigationTransition(
      selectedContentColor,
      unselectedContentColor,
      selected
    ) {
      Column {
        Box(
          Modifier
            .align(Alignment.CenterHorizontally)
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) selectedIconBackground else unselectedIconBackground)
            .padding(12.dp, 6.dp)
            .width(42.dp)
        ) {
          CustomIcon(
            modifier = Modifier.align(Alignment.Center),
            iconResId = iconResId,
            iconSize = IconSize.Medium,
            iconColor = if (selected) selectedContentColor else unselectedContentColor,
          )
        }
        if (label != null) {
          Text(
            label,
            style = AppTheme.typography.label2Medium,
            fontWeight = FontWeight(
              when {
                boldIfSelected && selected -> 700
                else -> 500
              }
            ),
            color = if (selected) selectedContentColor else unselectedContentColor,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Visible,
            softWrap = false,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavigationTransition(
  activeColor: Color,
  inactiveColor: Color,
  selected: Boolean,
  content: @Composable () -> Unit
) {
  val animationProgress by animateFloatAsState(
    targetValue = if (selected) 1f else 0f,
    animationSpec = TweenSpec(
      durationMillis = 600,
      easing = FastOutSlowInEasing
    )
  )
  val color = lerp(
    inactiveColor,
    activeColor,
    animationProgress
  )
  CompositionLocalProvider(
    LocalContentColor provides color.copy(alpha = 1f),
    LocalContentAlpha provides color.alpha
  ) {
    content()
  }
}

data class BottomNavigationItem(
  val label: String,
  @DrawableRes val iconResId: Int,
  val onClickAction: () -> Unit
)