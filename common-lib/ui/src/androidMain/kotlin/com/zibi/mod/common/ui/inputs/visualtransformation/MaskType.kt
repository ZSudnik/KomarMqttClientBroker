package com.zibi.mod.common.ui.inputs.visualtransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.core.text.isDigitsOnly

enum class MaskType(val keyboardType: KeyboardType) : OffsetMapping {
  BLIK(
    keyboardType = KeyboardType.Number
  ) {
    override fun getTextValue(text: String) = text.run { if (length < 6) this else substring(0..5) }

    override fun getDisplayText(text: AnnotatedString): String {
      val trimmed = getTextValue(text.text)

      return StringBuilder().apply {
        for (i in trimmed.indices) {
          append(trimmed[i])
          if (i == 2) append(" ")
        }
      }.toString()
    }

    override fun filter(text: String): Boolean = text.isDigitsOnly() && text.length <= 6

    override fun originalToTransformed(offset: Int): Int {
      if (offset <= 2) return offset
      if (offset <= 6) return offset + 1
      return 7
    }

    override fun transformedToOriginal(offset: Int): Int {
      if (offset <= 3) return offset
      if (offset <= 7) return offset - 1
      return 6
    }
  },
  POST_CODE(
    keyboardType = KeyboardType.Number
  ) {

    override fun getTextValue(text: String): String {
      val trimmed = text.run { if (length >= 5) substring(0..4) else this }

      return StringBuilder().apply {
        for (i in trimmed.indices) {
          append(trimmed[i])
          if (i == 1) append("-")
        }
      }.toString()
    }

    override fun getDisplayText(text: AnnotatedString): String {
      return getTextValue(text.text)
    }

    override fun filter(text: String): Boolean = text.isDigitsOnly() && text.length <= 5

    override fun originalToTransformed(offset: Int): Int {
      if (offset <= 1) return offset
      if (offset <= 5) return offset + 1
      return 6
    }

    override fun transformedToOriginal(offset: Int): Int {
      if (offset <= 2) return offset
      if (offset <= 6) return offset - 1
      return 5
    }
  },
  ;

  /**
   * Returns string displayed in input.
   * For example for BLIK code you want to display 123 456 but you want to type just 123456,
   * for post code you want to display 00-123 but you want to type 00123
   */
  abstract fun getDisplayText(text: AnnotatedString): String

  /**
   * Returns real text input value
   * For example for BLIK code you want to display 123 456 but you want to get just 123456 value,
   * for post code you want to display 00-123 and get the same value as well
   */
  abstract fun getTextValue(text: String): String

  /**
   * Filter input field value
   */
  abstract fun filter(text: String): Boolean
}
