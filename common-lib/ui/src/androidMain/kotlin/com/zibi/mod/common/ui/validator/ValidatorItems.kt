package com.zibi.mod.common.ui.validator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.theme.AppTheme

data class ValidatorItem(
  val isValid: Boolean,
  val label: String,
)

@Composable
fun ValidatorItems(
  modifier: Modifier = Modifier,
  validatorItems: List<ValidatorItem>,
) {
  Row(
    modifier = modifier,
  ) {
    val size = validatorItems.size
    if (size > 0)
      Column(
        modifier = Modifier.weight(1f)
      ) {
        for (index in 0..size / 2) {
          ValidatorItem(
            validatorItem = validatorItems[index],
          )
          Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        }
      }
    if (size > 1) {
      Spacer(modifier = Modifier.width(AppTheme.dimensions.regularPadding))
      Column(
        modifier = Modifier.weight(0.8f)
      ) {
        for (index in size / 2 + 1 until size) {
          ValidatorItem(
            validatorItem = validatorItems[index],
          )
          Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        }
      }
    }
  }

}

@Composable
fun ValidatorItem(
  modifier: Modifier = Modifier,
  validatorItem: ValidatorItem,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .size(AppTheme.dimensions.statusWithoutIconSize)
        .clip(CircleShape)
        .background(
          if (validatorItem.isValid) AppTheme.colors.green900
          else AppTheme.colors.inputFieldBorder
        )
    )
    Spacer(modifier = Modifier.width(AppTheme.dimensions.mediumPadding))
    Text(
      text = validatorItem.label,
      style = AppTheme.typography.body2Regular,
      color = if (validatorItem.isValid)
        AppTheme.colors.black
      else
        AppTheme.colors.inputFieldText
    )
  }
}

@Preview
@Composable
fun ValidatorItemsPreview() {
  ValidatorItems(
    validatorItems = listOf(
      ValidatorItem(
        isValid = true,
        label = "Minimum 8 znaków"
      ),
      ValidatorItem(
        isValid = false,
        label = "Znak specjalny"
      ),
      ValidatorItem(
        isValid = true,
        label = "Duża litera"
      ),
      ValidatorItem(
        isValid = true,
        label = "Mała litera"
      ),
      ValidatorItem(
        isValid = false,
        label = "Cyfra"
      ),
    )
  )
}

@Preview
@Composable
fun ValidatorItemPreviewIsValid() {
  ValidatorItem(
    validatorItem = ValidatorItem(
      label = "Przykładowa etykieta",
      isValid = true,
    )
  )
}
@Preview
@Composable
fun ValidatorItemPreviewIsNotValid() {
  ValidatorItem(
    validatorItem = ValidatorItem(
      label = "Przykładowa etykieta",
      isValid = false,
    )
  )
}