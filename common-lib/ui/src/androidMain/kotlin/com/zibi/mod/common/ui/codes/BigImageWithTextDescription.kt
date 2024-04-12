package com.zibi.mod.common.ui.codes

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun BigImageWithTextDescription(
  modifier: Modifier = Modifier,
  image: Bitmap,
  imageDescription: String,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      modifier = modifier,
      bitmap = image.asImageBitmap(),
      contentDescription = null
    )
    Text(
      textAlign = TextAlign.Center,
      text = imageDescription,
      style = AppTheme.typography.body2Regular,
    )
  }
}

