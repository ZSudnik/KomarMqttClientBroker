package com.zibi.mod.common.ui.controllers.select

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.zibi.mod.common.ui.theme.AppTheme

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ControllerSelect(
  items: List<String>,
  selectedItemIndex: Int? = null,
  selectedBackgroundColor: Color = AppTheme.colors.secondary,
  selectedTextColor: Color = AppTheme.colors.primary,
  onClick: (Int) -> Unit
) {
  val selectedValueIndex = remember { mutableStateOf(selectedItemIndex) }
  val isSelectedItem: (Int) -> Boolean = { selectedValueIndex.value == it }
  val onChangeState: (Int) -> Unit = { selectedValueIndex.value = it }

  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.smallPadding),
    content = {
      items(count = items.size,
        itemContent = {
          Box(
            modifier = Modifier
              .background(
                color = if (isSelectedItem(it)) selectedBackgroundColor
                else Color.Transparent,
                shape = RoundedCornerShape(50)
              )
              .clickable(interactionSource = MutableInteractionSource(),
                indication = null,
                enabled = true,
                onClickLabel = null,
                onClick = {
                  onChangeState(it)
                  onClick(it)
                })
              .padding(
                start = AppTheme.dimensions.mediumPadding,
                end = AppTheme.dimensions.mediumPadding
              )
              .height(AppTheme.dimensions.controllerButtonHeight)
              .wrapContentWidth(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = items[it],
              textAlign = TextAlign.Center,
              style = AppTheme.typography.bodyMedium,
              color = if (isSelectedItem(it)) selectedTextColor else Color.Black,
              maxLines = 1,
              modifier = Modifier.widthIn(AppTheme.dimensions.controllerSelectMinimumWidth),
            )
          }

        })
    })
}
