package com.zibi.mod.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object AppTheme {

  val colors: ThemeColors
    @Composable @ReadOnlyComposable get() = LocalColors.current

  val dimensions: Dimensions
    @Composable @ReadOnlyComposable get() = LocalDimension.current

  val shapes: Shapes
    @Composable @ReadOnlyComposable get() = LocalShape.current

  val typography: Typography
    @Composable @ReadOnlyComposable get() = LocalTypography.current
}

@Composable
fun MainTheme(
  shapes: Shapes = AppTheme.shapes,
  dimensions: Dimensions = AppTheme.dimensions,
  typography: Typography = AppTheme.typography,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  /*
  blokowanie dark theme jest przez zapis w ustawie o wygladzie dokumentow,
  do usuniecia w BaseActivity i MainActivity gdy bedziemy przechodzic na dark mode
  AppCompatDelegate.setDefaultNightMode(
          AppCompatDelegate.MODE_NIGHT_NO
  ) */

  val colors = if (darkTheme) {
    ThemeColors.DARK
  } else {
    ThemeColors.LIGHT
  }

  CompositionLocalProvider(
    LocalColors provides colors,
    LocalDimension provides dimensions,
    LocalShape provides shapes,
    LocalTypography provides typography
  ) {
    content()
  }

}