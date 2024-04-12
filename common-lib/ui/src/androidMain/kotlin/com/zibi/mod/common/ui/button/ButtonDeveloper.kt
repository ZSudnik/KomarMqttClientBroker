package com.zibi.mod.common.ui.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ButtonDeveloper(
  text: String,
  onClickAction: () -> Unit,
  modifier: Modifier = Modifier
    .fillMaxWidth()
    .padding(
      start = 16.dp,
      end = 16.dp
    ),
  id: String = "undefined"
) {
  Button(
    onClick = onClickAction,
    modifier = modifier
  ) {
    Text(text = text,
      modifier = Modifier
        .semantics { testTagsAsResourceId = true }
        .semantics { testTag = id })
  }
}