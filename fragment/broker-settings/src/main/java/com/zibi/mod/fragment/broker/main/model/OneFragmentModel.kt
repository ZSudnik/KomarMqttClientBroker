package com.zibi.mod.fragment.broker.main.model

import com.zibi.mod.common.ui.cards.cardBasic.CardBasicItem
import com.zibi.mod.fragment.broker.domain.model.Colors

data class OneFragmentModelInit(
  val itemList: List<CardBasicItem.Icon>,
  ) {
    fun hasItems() = itemList.isNotEmpty()
  }

data class OneFragmentModelOne(
    val item: CardBasicItem.Icon,
    val colors: Colors,
)

data class ButtonModel(
  val bottomButtonText: String,
  val onBottomNavigationButton: () -> Unit,
)
