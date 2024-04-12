package com.zibi.mod.common.ui.document.logo

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zibi.mod.common.ui.contentBox.ContentBox
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun DocumentLogo(
  modifier: Modifier = Modifier,
  text: String,
  image: Bitmap?,
  textColor: Color = AppTheme.colors.black,
  backgroundColor: Color = Color.White,
) {
  val logoMaxHeight = 42.dp
  val xRegularPadding = AppTheme.dimensions.xRegularPadding

  ContentBox(
    modifier = modifier,
    padding = AppTheme.dimensions.zero,
    backgroundColor = backgroundColor,
    elevation = AppTheme.dimensions.zero,
    content = {
      ConstraintLayout(
        modifier = Modifier
          .padding(xRegularPadding)
          .fillMaxWidth(),
      ) {
        val (logoTextRef, logoImageRef) = createRefs()

        Text(
          text = text,
          color = textColor,
          style = AppTheme.typography.bodyMedium,
          textAlign = TextAlign.Start,
          modifier = Modifier
            .fillMaxWidth()
            .constrainAs(logoTextRef) {
              top.linkTo(parent.top)
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(logoImageRef.start, margin = xRegularPadding)
              width = Dimension.fillToConstraints
            }
        )

        Box(modifier = Modifier.constrainAs(logoImageRef) {
          top.linkTo(parent.top)
          bottom.linkTo(parent.bottom)
          end.linkTo(parent.end)
        }) {
          if (image != null) {
            Image(
              bitmap = image.asImageBitmap(),
              modifier = Modifier.height(logoMaxHeight),
              contentDescription = null
            )
          }
        }
      }
    }
  )
}