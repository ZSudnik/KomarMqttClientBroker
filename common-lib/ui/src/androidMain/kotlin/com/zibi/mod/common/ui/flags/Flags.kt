package com.zibi.mod.common.ui.flags

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.delay
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.opengl.TypeShader
import com.zibi.mod.common.ui.opengl.view.DisplayComposeGL


@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun FlagWavingDefaultSVG(
  modifier: Modifier = Modifier,
  @DrawableRes resFlag: Int
) {
  val image = AnimatedImageVector.animatedVectorResource(resFlag)
  var atEnd by remember { mutableStateOf(false) }
  Icon(
    painter = rememberAnimatedVectorPainter(
      image,
      atEnd
    ),
    modifier = modifier,
    contentDescription = null
  )
  LaunchedEffect(Unit) {
    if (!atEnd) {
      delay(500)
      atEnd = !atEnd
    }
  }

}

@Composable
fun FlagWavingDefaultGL(
  modifier: Modifier = Modifier,
  @DrawableRes resFlag: Int
) {

  if (LocalInspectionMode.current) { // usunac if{}else{} i cala zawartosc za if gdy zacznie dzialac preview dla
    // AndroidView
    val context = LocalContext.current
    val bitmap: Bitmap = remember {
      AppCompatResources.getDrawable(
        context,
        resFlag
      )!!.toBitmap()
    }
    Box(
      contentAlignment = Alignment.BottomCenter,
    ) {
      Image(
        modifier = modifier,
        bitmap = bitmap.asImageBitmap(),
        contentScale = ContentScale.FillBounds,
        contentDescription = "bitmap"
      )
    }
  } else {
    DisplayComposeGL(
      modifier = modifier,
      listResBitmap = listOf(resFlag),
      typeShader = TypeShader.WavingBitmap,
      isPreview = false
    )
  }
}

@Composable
fun FlagPolish(modifier: Modifier = Modifier) {
  FlagWavingDefaultGL(
    modifier = modifier,
    R.drawable.common_ui_ic_polish_flag
  )
}

@Composable
fun FlagUkrainian(modifier: Modifier = Modifier) {
  FlagWavingDefaultGL(
    modifier = modifier,
    R.drawable.common_ui_ic_ukrainian_flag
  )
}

@Composable
fun FlagEnglish(modifier: Modifier = Modifier) {
  FlagWavingDefaultGL(
    modifier = modifier,
    R.drawable.common_ui_ic_english_flag
  )
}


@Preview
@Composable
fun TestFlag() {
  Card(
    modifier = Modifier
      .padding(10.dp)
      .wrapContentHeight(),
    shape = RoundedCornerShape(0),
    elevation = 0.dp,
    backgroundColor = Color.Transparent
  ) {

    FlagEnglish(
      Modifier.size(
        width = 600.dp,
        height = 250.dp
      )
    )


  }


}


