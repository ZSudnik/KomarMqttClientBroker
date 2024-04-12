package com.zibi.mod.common.ui.cards.cardBasic

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.zibi.mod.common.ui.R

@Composable
fun ExpandableCardBasicList(
  modifier: Modifier = Modifier,
  title: String,
  items: List<CardBasicItem>,
  initialExpanded: Boolean = false,
) {
  var expanded by remember {
    mutableStateOf(initialExpanded)
  }
  val headerExpanded = CardBasicItem.Icon(
    title = title,
    iconForwardResId = R.drawable.common_ui_ic_chevron_up_bold,
    onClick = {
      expanded = !expanded
    }
  )
  val headerCollapsed = CardBasicItem.Icon(
    title = title,
    iconForwardResId = R.drawable.common_ui_ic_chevron_down_bold,
    onClick = {
      expanded = !expanded
    }
  )
  CardBasicList(
    modifier = modifier,
    animateSizeChange = true,
    items = if (!expanded) {
      listOf(headerCollapsed)
    } else {
      listOf(headerExpanded) + items
    }
  )
}