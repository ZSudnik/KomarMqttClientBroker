package com.zibi.mod.common.ui.loader

//import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R

@Composable
fun Loader(
  modifier: Modifier = Modifier.size(AppTheme.dimensions.loaderSize)
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(
      resId = if (AppTheme.colors.isLight) R.raw.light_theme_loader else R.raw.dark_theme_loader
    )
  )
  val interactionSource = remember { MutableInteractionSource() }
  val lottieProgress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever,
  )
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        color = AppTheme.colors.semiTransparentBackground,
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
      ) { /* Prevent click */ },
    contentAlignment = Alignment.Center,
  ) {
    LottieAnimation(
      composition = composition,
      speed = lottieProgress,
//      progress = lottieProgress,
      modifier = modifier,
    )
  }

//  BackHandler { /*Handled back action*/ }
}
