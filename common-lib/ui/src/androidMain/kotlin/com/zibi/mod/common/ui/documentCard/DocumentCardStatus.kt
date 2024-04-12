package com.zibi.mod.common.ui.documentCard

import androidx.annotation.DrawableRes
import com.zibi.mod.common.ui.status.StatusType

data class DocumentCardStatus(
  val text: String,
  val type: StatusType,
  @DrawableRes val iconResId: Int,
)
