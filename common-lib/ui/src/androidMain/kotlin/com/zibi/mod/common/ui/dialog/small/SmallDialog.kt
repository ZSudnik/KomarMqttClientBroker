package com.zibi.mod.common.ui.dialog.small

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun DecisionDialog(
  dialog: SmallDialogType,
  onDismissRequest: () -> Unit,
) {
  val rippleInteractionSource = remember { NoRippleInteractionSource() }
  Dialog(
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
    ),
    onDismissRequest = onDismissRequest,
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = AppTheme.dimensions.regularPadding),
      shape = RoundedCornerShape(8.dp),
    ) {
      Column(
        modifier = Modifier
          .background(
            color = Color.White,
          )
          .padding(all = AppTheme.dimensions.xRegularPadding),
      ) {
        Text(
          text = dialog.title,
          style = AppTheme.typography.title,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Start,
          color = AppTheme.colors.black
        )
        if (dialog.content != null) {
          Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
          Text(
            text = dialog.content,
            style = AppTheme.typography.body1Regular,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            color = AppTheme.colors.grey
          )
        }

        Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))

        Row(
          modifier = Modifier
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
        ) {
          if (dialog.negativeButtonText != null && dialog.onNegativeButtonClick != null) {
            TextButton(
              modifier = Modifier,
              interactionSource = rippleInteractionSource,
              onClick = dialog.onNegativeButtonClick,
            ) {
              Text(
                text = dialog.negativeButtonText,
                style = AppTheme.typography.subtitleRegular,
                color = AppTheme.colors.statusRedWeb,
              )
            }
            Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
          }

          if (dialog.positiveButtonText != null && dialog.onPositiveButtonClick != null) {
            TextButton(
              modifier = Modifier,
              interactionSource = rippleInteractionSource,
              onClick = dialog.onPositiveButtonClick
            ) {
              Text(
                text = dialog.positiveButtonText,
                style = AppTheme.typography.subtitleRegular,
                color = AppTheme.colors.primary,
              )
            }
            Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
          }

          if (dialog.cancelButtonText != null && dialog.onCancelButtonClick != null) {
            TextButton(
              modifier = Modifier,
              interactionSource = rippleInteractionSource,
              onClick = dialog.onCancelButtonClick
            ) {
              Text(
                text = dialog.cancelButtonText,
                style = AppTheme.typography.subtitleRegular,
                color = AppTheme.colors.statusGreen1,
              )
            }
           }
        }
      }
    }
  }
}

@Composable
@Preview
fun DecisionDialogPreview() {
  DecisionDialog(
    dialog = SmallDialogType.Default(
      title = "Zgoda na użycie aparatu",
      content = "Aby zeskanować kod QR, zezwól aplikacji mObywatel na użycie aparatu.",
      positiveButtonText = "Zgadzam się",
      negativeButtonText = "Nie teraz",
      cancelButtonText = "Porzuc",
      onPositiveButtonClick = {},
      onNegativeButtonClick = {},
      onCancelButtonClick = {},
    ),
    onDismissRequest = {},
  )
}