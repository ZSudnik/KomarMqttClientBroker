package com.zibi.mod.common.ui.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.model.SelectableItem
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun CheckBoxes(
  modifier: Modifier = Modifier,
  items: List<SelectableItem>,
  checkedColor: Color = AppTheme.colors.primary,
  onItemClick: (SelectableItem) -> Unit
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.mediumPadding)
  ) {
    items.forEach { item ->
      BasicCheckBox(
        item = item,
        onItemClick = onItemClick,
        checkedColor = checkedColor
      )
    }
  }
}

@Preview
@Composable
fun CheckBoxesPreview() {
  CheckBoxes(items = listOf(
    SelectableItem(
      "Option 3",
      true
    ),
    SelectableItem(
      "Option 2",
      true
    ),
    SelectableItem(
      "Option 3",
      false
    )
  ),
    onItemClick = {})
}