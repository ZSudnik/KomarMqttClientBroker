package com.zibi.mod.common.ui.cards.cardBasic

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color

sealed class CardBasicItem(
  val title: String,
  val description: String? = null,
  val info: String? = null,
  val iconResId: Int? = null,
  val isEnabled: Boolean = true,
  val onClick: (() -> Unit)? = null,
) {

  class Default(
    title: String,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onClick,
  )

  class Icon(
    title: String,
    val iconForwardResId: Int,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onClick,
  )

  class ClickableIcon(
    title: String,
    val iconForwardResId: Int,
    val iconForwardTint: Color = Color.Unspecified,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    isEnabled: Boolean = true,
    onIconClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onIconClick,
  )

  class SwitchButton(
    title: String,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    val isChecked: Boolean,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onClick,
  )

  class CheckBox(
    title: String,
    val isChecked: Boolean,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onClick,
  )

  class RadioButton(
    title: String,
    val isSelected: Boolean,
    description: String? = null,
    info: String? = null,
    iconResId: Int? = null,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    isEnabled = isEnabled,
    onClick = onClick,
  )

  class ImageWithSmallButton(
    title: String,
    val image: Bitmap,
    val buttonText: String,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    onClick = onClick,
  )

  class ClickableButton(
    title: String,
    description: String? = null,
    info: String? = null,
    val buttonText: String,
    iconResId: Int? = null,
    onClick: (() -> Unit)? = null,
  ) : CardBasicItem(
    title = title,
    description = description,
    info = info,
    iconResId = iconResId,
    onClick = onClick,
  )

  class BigImageWithTextDescription(
    title: String,
    val image: Bitmap,
    val imageDescription: String,
  ) : CardBasicItem(
    title = title,
  )
}
