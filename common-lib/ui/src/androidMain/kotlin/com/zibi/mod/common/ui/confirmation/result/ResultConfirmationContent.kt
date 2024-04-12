package com.zibi.mod.common.ui.confirmation.result

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.button.ButtonData
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.model.KeyValueData
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.mod.common.ui.R

interface ResultConfirmationData {
  @get:DrawableRes val icon: Int
  val title: String
  val primaryKeyValue: KeyValueData
  val secondaryKeyValue: KeyValueData
  val primaryButton: ButtonData
  val secondaryButton: ButtonData
  val tertiaryButton: ButtonData
  val closeButton: ButtonData
}

data class ResultConfirmationErrorData(
  override val title: String,
  override val icon: Int = R.drawable.common_ui_ic_result_error,
  override val primaryKeyValue: KeyValueData = KeyValueData(title = "", description = ""),
  override val secondaryKeyValue: KeyValueData = KeyValueData(title = "", description = ""),
  override val primaryButton: ButtonData,
  override val closeButton: ButtonData = ButtonData {},
  override val secondaryButton: ButtonData = ButtonData {},
  override val tertiaryButton: ButtonData = ButtonData {},
) : ResultConfirmationData

fun ResultConfirmationErrorData.assignActions(
  primaryAction: () -> Unit,
  secondaryAction: () -> Unit = {},
  tertiaryAction: () -> Unit = {},
  closeAction: () -> Unit = {},
): ResultConfirmationErrorData {
  return this.copy(
    primaryButton = this.primaryButton.copy {
      primaryAction()
    },
    secondaryButton = this.secondaryButton.copy {
      secondaryAction()
    },
    tertiaryButton = this.tertiaryButton.copy {
      tertiaryAction()
    },
    closeButton = this.closeButton.copy {
      closeAction()
    }
  )
}

class ResultConfirmationMockData(
  override val closeButton: ButtonData = ButtonData {},
) : ResultConfirmationData {
  override val icon = R.drawable.common_ui_ic_result_success
  override val title = "Dziękujemy"
  override val primaryKeyValue = KeyValueData(
    title = "Dokonałeś opłaty:",
    description = "Opłata za przekształcenie gruntów Gminy Lublin",
  )
  override val secondaryKeyValue = KeyValueData(
    title = "Kwota",
    description = "822,00 zł",
  )
  override val primaryButton = ButtonData(text = "Pobierz potwierdzenie") {}
  override val secondaryButton = ButtonData(text = "Wróc do pulpitu") {}
  override val tertiaryButton = ButtonData(text = "Wszystkie płatności") {}
}

@Composable
fun ResultConfirmationContent(
  data: ResultConfirmationData
) {

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colors.background),
  ) {
    TopMenu(
      title = "",
      iconMenuResId = R.drawable.common_ui_ic_close_bold,
      onIconMenuClick = data.closeButton.onClick,
    )
    Column(
      modifier = Modifier.padding(horizontal = AppTheme.dimensions.regularPadding),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .weight(
            1F,
            fill = true
          ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        CustomIcon(
          iconResId = data.icon,
          iconSize = IconSize.XBig,
          iconColor = Color.Unspecified,
          contentDescription = null, // TODO MOB-6712 update contentDescription
        )
        Text(
          modifier = Modifier.padding(all = AppTheme.dimensions.xLargePadding),
          text = data.title,
          style = AppTheme.typography.title,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
        Text(
          text = data.primaryKeyValue.title,
          style = AppTheme.typography.body1Regular,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        Text(
          text = data.primaryKeyValue.description,
          style = AppTheme.typography.title,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
        Text(
          text = data.secondaryKeyValue.title,
          style = AppTheme.typography.body1Regular,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        Text(
          text = data.secondaryKeyValue.description,
          style = AppTheme.typography.title,
        )
      }

      Spacer(modifier = Modifier.height(AppTheme.dimensions.bottomSheetBottomPadding))

      Column(
        modifier = Modifier.wrapContentWidth(),
        verticalArrangement = Arrangement.Bottom
      ) {
        if (data.primaryButton.text.isNotEmpty()) {
          ButtonLarge(
            text = data.primaryButton.text,
            onClick = data.primaryButton.onClick
          )
        }
        if (data.secondaryButton.text.isNotEmpty()) {
          Spacer(
            modifier = Modifier.height(AppTheme.dimensions.mediumPadding)
          )
          ButtonLarge(
            text = data.secondaryButton.text,
            style = ButtonLargeStyle.SECONDARY,
            onClick = data.secondaryButton.onClick
          )
        }
        if (data.tertiaryButton.text.isNotEmpty()) {
          Spacer(
            modifier = Modifier.height(AppTheme.dimensions.mediumPadding)
          )
          ButtonLarge(
            text = data.tertiaryButton.text,
            style = ButtonLargeStyle.TERTIARY,
            onClick = data.tertiaryButton.onClick
          )
        }
        Spacer(
          modifier = Modifier.height(AppTheme.dimensions.bottomSheetBottomPadding)
        )
      }
    }
  }
}

@Preview
@Composable
fun ResultConfirmationModalPreview() {
  ResultConfirmationContent(
    data = ResultConfirmationMockData()
  )
}
