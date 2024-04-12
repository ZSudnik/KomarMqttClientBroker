package com.zibi.mod.common.ui.documentCard

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class DocumentCardItem(
  val title: String,
  val showLabel: String,
  @ColorRes val primaryColor: Color,
  @ColorRes val secondaryColor: Color,
  @DrawableRes val iconResId: Int,
  @DrawableRes val logoResId: Int?,
  @DrawableRes val bottomBarResId: Int,
  val status: DocumentCardStatus? = null,
  val onClick: () -> Unit = {},
)