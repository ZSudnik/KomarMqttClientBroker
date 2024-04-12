package com.zibi.mod.common.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

data class Shapes(
  val basicDialog: Shape = RoundedCornerShape(16.dp),
  val confirmationModal: Shape = RoundedCornerShape(32.dp),
  val passwordCard: Shape = RoundedCornerShape(12.dp)
)

internal val LocalShape = staticCompositionLocalOf { Shapes() }