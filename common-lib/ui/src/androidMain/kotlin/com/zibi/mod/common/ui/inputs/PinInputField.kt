package com.zibi.mod.common.ui.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.zibi.mod.common.ui.theme.AppTheme
import kotlin.time.Duration.Companion.seconds

private const val DOT_CHAR = '\u2022'
private val CORNER_RADIUS = 8.dp
private val HIDE_CHARACTER_DELAY = 2.seconds

@Composable
fun PinInputField(
  modifier: Modifier = Modifier,
  value: String,
  length: Int = 4,
  onValueChange: (String) -> Unit,
) {
  BasicTextField(
    modifier = modifier,
    value = TextFieldValue(value, selection = TextRange(value.length)),
    onValueChange = {
      if (it.text.length <= length) {
        onValueChange(it.text)
      }
    },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
    decorationBox = {
      Row(horizontalArrangement = Arrangement.Center) {
        repeat(length) { index ->
          PinCharacterBox(
            characterIndex = index,
            pinValue = value,
          )
          if (index == length - 1) return@repeat
          Spacer(modifier = Modifier.width(AppTheme.dimensions.regularPadding))
        }
      }
    }
  )
}

@Composable
private fun PinCharacterBox(
  characterIndex: Int,
  pinValue: String,
) {
  val isFocused = characterIndex == pinValue.length
  var shouldMaskPinValue by remember { mutableStateOf(false) }

  val boxCharacter = when {
    characterIndex >= pinValue.length -> ""
    shouldMaskPinValue || characterIndex + 2 <= pinValue.length -> DOT_CHAR
    else -> pinValue[characterIndex]
  }.toString()

  Box(
    modifier = Modifier
      .size(
        width = AppTheme.dimensions.pinInputBoxWidth,
        height = AppTheme.dimensions.pinInputBoxHeight
      )
      .clip(RoundedCornerShape(CORNER_RADIUS))
      .border(
        width = if (isFocused) 2.dp else 1.dp,
        color = if (isFocused) AppTheme.colors.primary else AppTheme.colors.inputFieldBorder,
        shape = RoundedCornerShape(CORNER_RADIUS),
      )
      .background(AppTheme.colors.white),
  ) {
    Text(
      modifier = Modifier.align(alignment = Alignment.Center),
      text = boxCharacter,
      style = AppTheme.typography.headline,
      textAlign = TextAlign.Center,
    )
  }

  LaunchedEffect(key1 = pinValue.length) {
    shouldMaskPinValue = (characterIndex < pinValue.length).apply {
      if (this) delay(HIDE_CHARACTER_DELAY)
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PinInputFieldPreview() {
  PinInputField(
    value = "123",
    onValueChange = { },
  )
}
