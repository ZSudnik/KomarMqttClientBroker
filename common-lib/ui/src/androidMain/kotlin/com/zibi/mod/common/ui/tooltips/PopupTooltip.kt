package com.zibi.mod.common.ui.tooltips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.zibi.mod.common.ui.theme.AppTheme

class TooltipDownArrow(
  private val cornerRadius: Float,
  private val arrowWidth: Float,
  private val arrowHeight: Float,
  private val arrowEndPadding: Float
) : Shape {
  override fun createOutline(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
  ): Outline {
    return Outline.Generic(Path().apply {
      reset()
      moveTo(
        cornerRadius,
        0f
      )

      lineTo(
        size.width,
        0f
      )

      arcTo(
        rect = Rect(
          offset = Offset(
            size.width - cornerRadius,
            0f
          ),
          size = Size(
            cornerRadius,
            cornerRadius
          )
        ),
        startAngleDegrees = 270f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )

      lineTo(
        size.width,
        size.height - cornerRadius
      )

      arcTo(
        rect = Rect(
          offset = Offset(
            size.width - cornerRadius,
            size.height - cornerRadius
          ),
          size = Size(
            cornerRadius,
            cornerRadius
          )
        ),
        startAngleDegrees = 0f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )

      lineTo(
        size.width - arrowEndPadding,
        size.height
      )

      lineTo(
        size.width - arrowEndPadding - (arrowWidth / 2),
        size.height + arrowHeight
      )

      lineTo(
        size.width - arrowEndPadding - arrowWidth,
        size.height
      )

      lineTo(
        cornerRadius,
        size.height
      )

      arcTo(
        rect = Rect(
          offset = Offset(
            0f,
            size.height - cornerRadius
          ),
          size = Size(
            cornerRadius,
            cornerRadius
          )
        ),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )

      lineTo(
        0f,
        cornerRadius
      )

      arcTo(
        rect = Rect(
          offset = Offset(
            0f,
            0f
          ),
          size = Size(
            cornerRadius,
            cornerRadius
          )
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
      )
      close()
    })
  }
}


class InputTooltipOffsetPositionProvider(
  private val infoButtonCoordinates: IntOffset,
  private val textLineCount: Int
) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize
  ): IntOffset {
    return infoButtonCoordinates.copy(
      x = windowSize.width - infoButtonCoordinates.x - popupContentSize.width - 15,
      y = infoButtonCoordinates.y - 15 - (44 * (textLineCount - 1))
    )
  }
}

@Composable
fun PopupTooltip(
  tooltipText: String,
  infoButtonCoordinates: IntOffset,
  onDismissRequest: () -> Unit
) {
  val density = LocalDensity.current

  var textLineCount by remember {
    mutableStateOf(1)
  }

  Popup(
    popupPositionProvider = InputTooltipOffsetPositionProvider(
      infoButtonCoordinates,
      textLineCount
    ),
    onDismissRequest = onDismissRequest,
  ) {
    Row(
      modifier = Modifier
        .widthIn(
          min = AppTheme.dimensions.minTooltipWidth,
          max = AppTheme.dimensions.maxTooltipWidth
        )
        .background(
          shape = TooltipDownArrow(cornerRadius = with(density) { AppTheme.dimensions.tooltipRadius.toPx() },
            arrowWidth = with(density) { AppTheme.dimensions.tooltipArrowWidth.toPx() },
            arrowHeight = with(density) { AppTheme.dimensions.tooltipArrowHeight.toPx() },
            arrowEndPadding = with(density) { AppTheme.dimensions.tooltipArrowPadding.toPx() }),
          color = AppTheme.colors.grey
        )
        .padding(
          horizontal = AppTheme.dimensions.regularPadding,
          vertical = AppTheme.dimensions.mediumPadding
        )
    ) {
      Text(text = tooltipText,
        style = AppTheme.typography.label2Regular,
        color = Color.White,
        onTextLayout = { textLayoutResult: TextLayoutResult ->
          textLineCount = textLayoutResult.lineCount
        })
    }
  }
}