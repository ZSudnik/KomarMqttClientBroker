package com.zibi.mod.common.ui.button.radio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun ButtonsRadio(
  modifier: Modifier = Modifier,
  items: List<RadioButtonData>,
  selectedColor: Color = AppTheme.colors.primary,
  onItemClick: (RadioButtonData) -> Unit
) {
  val selectedValue = remember { mutableStateOf(String()) }
  val isSelectedItem: (RadioButtonData) -> Boolean = { selectedValue.value == it.label }

  Column(
    modifier = modifier
  ) {
    items.forEachIndexed { index, item ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .selectable(
            selected = item.isSelected ?: isSelectedItem(item),
            onClick = {
              selectedValue.value = item.label
              onItemClick(item)
            },
            role = Role.RadioButton
          )
          .then(
            if (index > 0 && index != items.size) Modifier.padding(top = AppTheme.dimensions.xLargePadding) else
              Modifier
          )
      ) {
        RadioButton(
          selected = item.isSelected ?: isSelectedItem(item),
          onClick = null,
          colors = RadioButtonDefaults.colors(
            selectedColor = selectedColor,
            unselectedColor = AppTheme.colors.inputFieldBorder
          )
        )
        Text(
          text = item.label,
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.dimensions.regularPadding),
          style = AppTheme.typography.body1Regular
        )
      }
    }
  }
}

@Composable
@Preview
fun ButtonsRadioPreview() {
  ButtonsRadio(
    modifier = Modifier.padding(AppTheme.dimensions.mediumPadding),
    items = listOf(
      RadioButtonData("Active"),
      RadioButtonData("Inactive")
    )
  ) { _ -> }
}