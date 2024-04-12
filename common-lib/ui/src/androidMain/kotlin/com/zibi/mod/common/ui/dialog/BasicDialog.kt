package com.zibi.mod.common.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicDialog(
  dialogImage: Int? = null,
  dialogImageTint: Color = AppTheme.colors.primary,
  title: String,
  content: String,
  onDismissRequest: () -> Unit,
  firstButtonText: String,
  firstButtonAction: () -> Unit,
  secondButtonText: String? = null,
  secondButtonAction: () -> Unit = {}
) {
  Dialog(
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    ),
    onDismissRequest = onDismissRequest
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = AppTheme.dimensions.regularPadding),
      shape = AppTheme.shapes.basicDialog
    ) {
      Column(
        modifier = Modifier
          .background(
            color = Color.White
          )
          .padding(
            horizontal = AppTheme.dimensions.xRegularPadding,
            vertical = AppTheme.dimensions.xLargePadding
          ),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (dialogImage != null) {
          CustomIcon(
            iconResId = dialogImage,
            iconSize = IconSize.Big,
            iconColor = dialogImageTint,
            contentDescription = "dialog image", // TODO MOB-6712 update contentDescription
          )
          Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))
        }

        Text(
          text = title,
          style = AppTheme.typography.title,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          color = AppTheme.colors.black
        )
        if (content.isNotEmpty()) {
          Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))
          Text(
            text = content,
            style = AppTheme.typography.body1Regular,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = AppTheme.colors.grey
          )
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.xxLargePadding))

        ButtonLarge(
          modifier = Modifier.fillMaxWidth(),
          text = firstButtonText,
          style = ButtonLargeStyle.PRIMARY,
          onClick = firstButtonAction
        )
        if (secondButtonText != null) {
          Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
          ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            text = secondButtonText,
            style = ButtonLargeStyle.SECONDARY,
            onClick = secondButtonAction
          )
        }
      }
    }
  }
}