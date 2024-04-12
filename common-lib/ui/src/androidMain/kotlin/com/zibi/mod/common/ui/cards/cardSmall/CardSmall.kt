package com.zibi.mod.common.ui.cards.cardSmall

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.theme.AppTheme.dimensions
import com.zibi.mod.common.ui.theme.AppTheme.typography
import com.zibi.mod.common.ui.R

//@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun CardSmall(
  text: String,
  iconResId: Int,
  iconColor: Color = AppTheme.colors.primary,
  onClick: (() -> Unit)? = null
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val mutableInteractionSource = remember {MutableInteractionSource()  }
    Card(
      shape = RoundedCornerShape(size = dimensions.smallCardCornerRadius),
      backgroundColor = Color.White,
      elevation = dimensions.documentCardElevation,
      modifier = Modifier
        .clickable(
          interactionSource = mutableInteractionSource,
          indication = null,
          enabled = onClick != null,
          onClickLabel = null,
          onClick = { onClick?.let { it() } }
        )
        .size(dimensions.smallCardWidth),
    ) {
      CustomIcon(
        iconResId = iconResId,
        iconSize = IconSize.SBig,
        iconColor = iconColor,
      )
    }
    Spacer(
      modifier = Modifier.height(dimensions.xxMediumPadding)
    )
    Text(
      text = text,
      modifier = Modifier.width(dimensions.smallCardTextWidth),
      textAlign = TextAlign.Center,
      style = typography.body2RegularLight
    )
  }
}

@Preview
@Composable
fun PreviewSmallCard() {
  CardSmall(
    text = "Test",
    iconResId = R.drawable.common_ui_ic_refresh,
    onClick = {}
  )
}
