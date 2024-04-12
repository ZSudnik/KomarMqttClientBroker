package com.zibi.mod.common.ui.cards.cardBasic

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zibi.mod.common.ui.R

class CardBasicItemPreviewParameterProvider : PreviewParameterProvider<CardBasicItem> {
  override val values = sequenceOf(
    CardBasicItem.Icon(
      title = "Text and icon forward",
      iconForwardResId = R.drawable.common_ui_ic_chevron_right_bold,
    ),
    CardBasicItem.Icon(
      title = "Text, description and icon forward",
      description = "For better tests",
      iconForwardResId = R.drawable.common_ui_ic_chevron_right_bold,
    ),
    CardBasicItem.Icon(
      title = "Text, icon and icon forward",
      iconResId = R.drawable.common_ui_ic_icon_placeholder,
      iconForwardResId = R.drawable.common_ui_ic_chevron_right_bold,
    ),
    CardBasicItem.Icon(
      title = "Text, description, icon and icon forward",
      description = "For better tests",
      iconResId = R.drawable.common_ui_ic_icon_placeholder,
      iconForwardResId = R.drawable.common_ui_ic_chevron_right_bold
    ),
    CardBasicItem.Default(
      title = "Text and info",
      info = "Some additional info"
    ),
    CardBasicItem.ClickableButton(
      info = "Text and info",
      title = "Some additional info",
      buttonText = "Action text"
    ),
    CardBasicItem.SwitchButton(
      title = "Some additional info",
      description = "Some description",
      isChecked = true,
    ),
  )
}