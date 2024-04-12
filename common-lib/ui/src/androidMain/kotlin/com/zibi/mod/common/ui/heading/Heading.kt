package com.zibi.mod.common.ui.heading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.button.small.ButtonSmallWithText
import com.zibi.mod.common.ui.theme.AppTheme

@Composable
fun Heading(
  text: String,
  buttonSmall: @Composable (() -> Unit)? = null,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(
        start = AppTheme.dimensions.mediumPadding,
        end = AppTheme.dimensions.regularPadding
      ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = text,
      style = AppTheme.typography.title,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
    buttonSmall?.let { it() }
  }
}

@Preview
@Composable
fun HeadingPreview() {
  Heading(text = "Lorem ipsum",
    buttonSmall = {
      ButtonSmallWithText(text = "Wszystkie",
        onClick = { })
    })
}