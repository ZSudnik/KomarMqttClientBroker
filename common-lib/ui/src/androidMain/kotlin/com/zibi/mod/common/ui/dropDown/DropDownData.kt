package com.zibi.mod.common.ui.dropDown

data class DropDownData(
  val title: String = "",
  val items: List<String> = emptyList(),
  val selectedItem: String? = null,
  val onItemSelected: (String) -> Unit = {},
)