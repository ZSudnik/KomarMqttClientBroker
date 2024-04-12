package com.zibi.mod.common.ui.inputs.visualtransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(private val maskType: MaskType) : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    return TransformedText(AnnotatedString(maskType.getDisplayText(text)), maskType)
  }
}
