package com.zibi.mod.common.ui.cards.cardStatus

import com.zibi.mod.common.ui.status.StatusType

data class StatusData(
  val text: String,
  val type: StatusType,
  val iconResId: Int? = null,
)
