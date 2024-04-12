package com.zibi.mod.common.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zibi.mod.common.ui.R

object Typography {

  private val roboto: FontFamily
    get() {
      return FontFamily(
        Font(
          R.font.roboto_bold,
          FontWeight.Bold
        ),
        Font(
          R.font.roboto_medium,
          FontWeight.Medium
        ),
        Font(
          R.font.roboto_regular,
          FontWeight.Normal
        ),
        Font(
          R.font.roboto_light,
          FontWeight.Light
        )
      )
    }

  val headline: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
      )
    }

  val title: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
      )
    }

  val modalTitle: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 25.sp
      )
    }

  val subtitle: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
      )
    }

  val subtitleRegular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
      )
    }

  val bodyMedium: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
      )
    }

  val body1Regular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
      )
    }

  val body1RegularLight: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
      )
    }

  val body2Regular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
      )
    }

  val body2RegularLight: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
      )
    }

  val labelRegular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
      )
    }

  val labelRegularLight: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
      )
    }

  val small2Regular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 8.sp
      )
    }

  val label2Regular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
      )
    }

  val label2Medium: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
      )
    }

  val bodyBold: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
      )
    }

  val initialsRegular: TextStyle
    get() {
      return TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp
      )
    }
}

internal val LocalTypography = staticCompositionLocalOf { Typography }
