package com.zibi.mod.common.ui.confirmation.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.status.Status
import com.zibi.mod.common.ui.status.StatusType
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.mod.common.ui.R

@Composable
fun SummaryConfirmationScreen(
  screenTitle: String,
  onCloseButtonClicked: () -> Unit,
  badgeStatus: Status,
  title: String,
  additionalTitle: String? = null,
  contentList: List<ConfirmationScreenContent>,
  amountPrefixValueLabel: String,
  amountValueWithCurrency: String,
  primaryButton: @Composable () -> Unit,
  secondaryButton: (@Composable () -> Unit)?,
) {
  val statusType = when (badgeStatus) {
    is Status.InProgress -> StatusType.WARNING_STATUS
    is Status.Success -> StatusType.VALID_STATUS
    is Status.Warning -> StatusType.ERROR_STATUS
    is Status.Info -> StatusType.INFO_STATUS
  }

  val additionalTitleColor = when (badgeStatus) {
    is Status.InProgress -> AppTheme.colors.black
    is Status.Info -> AppTheme.colors.statusBlue1
    is Status.Success -> AppTheme.colors.greenText
    is Status.Warning -> AppTheme.colors.redText
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colors.white),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    TopMenu(
      title = screenTitle,
      iconMenuResId = R.drawable.common_ui_ic_close_bold,
      backgroundColor = AppTheme.colors.white,
      onIconMenuClick = onCloseButtonClicked,
    )
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.regularPadding,
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.zero
        )
        .weight(
          1F,
          fill = true
        ),
    ) {
      item {
        Status(
          text = badgeStatus.title,
          type = statusType,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))

        Text(
          text = title,
          style = AppTheme.typography.title,
          color = AppTheme.colors.black
        )

        additionalTitle?.let { additionalStatus ->
          Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
          Text(
            text = additionalStatus,
            style = AppTheme.typography.body2Regular,
            color = additionalTitleColor,
          )
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))
      }

      items(contentList) { content ->
        Spacer(
          modifier = Modifier.height(
            if (content.isImportant) {
              AppTheme.dimensions.xLargePadding
            } else {
              AppTheme.dimensions.mediumPadding
            }
          )
        )
        Text(
          text = content.title,
          style = AppTheme.typography.subtitleRegular,
          color = AppTheme.colors.black
        )
        Text(
          text = content.description,
          color = when {
            content.isHighlighted -> AppTheme.colors.red900
            content.isImportant -> AppTheme.colors.black
            else -> AppTheme.colors.grey
          },
          style = if (content.isImportant) {
            AppTheme.typography.bodyBold
          } else {
            AppTheme.typography.body2Regular
          }
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.zero,
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.zero
        )
    ) {
      Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))
      Text(
        text = amountPrefixValueLabel,
        style = AppTheme.typography.subtitleRegular,
        color = AppTheme.colors.black
      )
      Text(
        text = amountValueWithCurrency,
        color = AppTheme.colors.black,
        style = AppTheme.typography.bodyBold
      )
    }

    Column(
      modifier = Modifier
        .wrapContentWidth()
        .padding(
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.xLargePadding,
          AppTheme.dimensions.xRegularPadding,
          AppTheme.dimensions.xLargePadding
        ),
      verticalArrangement = Arrangement.Bottom
    ) {
      primaryButton()
      if (secondaryButton != null) {
        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        secondaryButton()
      }
    }
  }
}

@Preview
@Composable
fun SummaryConfirmationModalPreview() {
  val contentList = arrayListOf(
    ConfirmationScreenContent(
      "Odbiorca przelewu",
      "Urząd Miasta Lublin,\ncom. Króla Władysława Łokietka 1\n20-109 Lublin",
    ),
    ConfirmationScreenContent(
      "Tytuł zobowiązania",
      "DEC-672317/2022/N/1",
    ),
    ConfirmationScreenContent(
      "Identyfikator zobowiązania",
      "ABC1234567890123456",
    ),
    ConfirmationScreenContent(
      "Termin płatności",
      "21.09.2022",
    ),
    ConfirmationScreenContent(
      "Metoda płatności",
      "BLIK",
    ),
  )

  SummaryConfirmationScreen(
    screenTitle = "Szczegóły płatności",
    onCloseButtonClicked = {},
    badgeStatus = Status.Warning("Do zapłaty"),
    title = "Opłata za przekształcenie gruntów Gminy Lublin",
    additionalTitle = "Termin płatności 21.09.2022 (mija za 3 dni)",
    contentList = contentList,
    amountPrefixValueLabel = "Kwota",
    amountValueWithCurrency = "822,00 zł",
    primaryButton = {
      ButtonLarge(
        text = "Przejdź do płatności",
        style = ButtonLargeStyle.PRIMARY,
        onClick = {}
      )
    },
    secondaryButton = {
      ButtonLarge(
        text = "Wróć",
        style = ButtonLargeStyle.SECONDARY,
        onClick = {}
      )
    },
  )
}
