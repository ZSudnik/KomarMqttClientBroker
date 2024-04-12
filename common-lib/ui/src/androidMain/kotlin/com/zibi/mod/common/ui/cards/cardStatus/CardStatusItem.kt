package com.zibi.mod.common.ui.cards.cardStatus

import androidx.compose.ui.graphics.Color
import com.zibi.mod.common.ui.button.ButtonData

data class CardStatusItem(
  val title: String,
  val status: StatusData? = null,
  val additionalInfo: String? = null,
  val body: String? = null,
  val amount: String? = null,
  val amountTitle: String? = null,
  val currency: String? = null,
  val iconForwardResId: Int? = null,
  val barBackgroundColor: Color? = null,
  val primaryButtonData: ButtonData? = null,
  val secondaryButtonData: ButtonData? = null,
  val onItemClick: (() -> Unit)? = null,
)
