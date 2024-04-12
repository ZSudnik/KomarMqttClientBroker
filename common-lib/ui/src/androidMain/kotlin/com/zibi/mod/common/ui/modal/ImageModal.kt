package com.zibi.mod.common.ui.modal

import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R


@Composable
fun ImageModal(
  image: Bitmap? = null,
  buttonText: String,
  onButtonClick: () -> Unit,
) {
  val imageSize = 290.dp
  val modalHeight = 667.dp
  Card(
    modifier = Modifier
      .height(modalHeight),
    backgroundColor = Color.White,
    shape = RoundedCornerShape(
      topStart = AppTheme.dimensions.xLargePadding,
      topEnd = AppTheme.dimensions.xLargePadding,
      bottomStart = AppTheme.dimensions.zero,
      bottomEnd = AppTheme.dimensions.zero
    )
  ) {
    Column(
      modifier = Modifier
        .padding(
          top = AppTheme.dimensions.largePadding,
          start = AppTheme.dimensions.xRegularPadding,
          end = AppTheme.dimensions.xRegularPadding,
          bottom = AppTheme.dimensions.xxLargePadding,
        ),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .clip(
            RoundedCornerShape(AppTheme.dimensions.smallPadding)
          )
          .background(AppTheme.colors.modalBar)
          .width(AppTheme.dimensions.dropDownContentTopBarWidth)
          .height(AppTheme.dimensions.dropDownContentTopBarHeight)
      )
      Spacer(modifier = Modifier.weight(1F))
      if (image != null) {
        Image(
          bitmap = image.asImageBitmap(),
          contentDescription = null,
          contentScale = ContentScale.FillBounds,
          modifier = Modifier
            .padding(AppTheme.dimensions.xxLargePadding)
            .size(imageSize)
        )
      }
      Spacer(modifier = Modifier.weight(1F))
      ButtonLarge(
        text = buttonText,
        onClick = onButtonClick
      )
    }
  }
}

@Preview
@Composable
fun ImageModalPreview() {
  val context = LocalContext.current
  val bitmap: Bitmap? =
    AppCompatResources.getDrawable(
      context,
      R.drawable.common_ui_ic_result_success
    )?.toBitmap()
  ImageModal(
    image = bitmap,
    buttonText = "Zamknij",
    onButtonClick = {}
  )
}