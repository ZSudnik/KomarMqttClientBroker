package com.zibi.mod.common.ui.checkbox

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.zibi.mod.common.ui.model.SelectableItem
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun BasicCheckBox(
  modifier: Modifier = Modifier,
  item: SelectableItem,
  checkedColor: Color = AppTheme.colors.primary,
  onItemClick: (SelectableItem) -> Unit
) {
  var isChecked by remember { mutableStateOf(item.isSelected) }
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .selectable(selected = isChecked,
        onClick = {
          isChecked = isChecked.not()
          onItemClick.invoke(item.copy(isSelected = isChecked))
        })
      .padding(AppTheme.dimensions.mediumPadding)
  ) {
    RoundedCheckBox(
      isChecked = isChecked,
      checkedColor = checkedColor
    ) {
      isChecked = it
      onItemClick.invoke(item.copy(isSelected = it))
    }
    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = AppTheme.dimensions.regularPadding),
      text = item.title,
      style = AppTheme.typography.body1Regular
    )
  }
}