package com.zibi.mod.common.ui.dropDown

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownDialogLayout(
  sheetState: ModalBottomSheetState,
  showDropDownDialog: Boolean,
  dropDownData: DropDownData,
  onClickIconClose: () -> Unit,
  innerContent: @Composable () -> Unit,
) {
  ModalBottomSheetLayout(
    sheetState = sheetState,
    sheetContent = {
      DropDownDialog(
        title = dropDownData.title,
        items = dropDownData.items,
        selectedItem = dropDownData.selectedItem,
        onClickIconClose = onClickIconClose,
        onClickItem = dropDownData.onItemSelected,
      )
    },
    sheetBackgroundColor = Color.Transparent,
    sheetElevation = AppTheme.dimensions.zero,
    sheetShape = RoundedCornerShape(AppTheme.dimensions.zero)
  ) {
    innerContent()
  }

  LaunchedEffect(showDropDownDialog) {
    if (showDropDownDialog) {
      sheetState.show()
    } else {
      sheetState.hide()
    }
  }
}

@Composable
fun DropDownDialog(
  modifier: Modifier = Modifier,
  title: String,
  items: List<String>,
  selectedItem: String? = null,
  onClickIconClose: () -> Unit,
  onClickItem: (String) -> Unit,
) {
  Card(
    modifier = modifier,
    backgroundColor = Color.White,
    shape = RoundedCornerShape(
      topStart = AppTheme.dimensions.regularPadding,
      topEnd = AppTheme.dimensions.regularPadding,
      bottomStart = AppTheme.dimensions.zero,
      bottomEnd = AppTheme.dimensions.zero
    )
  ) {
    Column {
      Box {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppTheme.dimensions.xMediumPadding),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .clip(
                RoundedCornerShape(AppTheme.dimensions.smallPadding)
              )
              .background(AppTheme.colors.modalBar)
              .width(AppTheme.dimensions.dropDownContentTopBarWidth)
              .height(AppTheme.dimensions.dropDownContentTopBarHeight)
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Box {
            IconButton(
              modifier = Modifier.size(IconSize.SBig.dimension),
              enabled = true,
              onClick = { onClickIconClose() },
              interactionSource = NoRippleInteractionSource()
            ) {
              CustomIcon(
                modifier = Modifier.align(alignment = Alignment.BottomStart),
                iconResId = R.drawable.common_ui_ic_close_bold,
                iconSize = IconSize.Small,
                contentDescription = "Close", // TODO MOB-6712 update contentDescription
              )
            }
          }
        }
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = AppTheme.dimensions.xxMediumPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          style = AppTheme.typography.bodyMedium,
          color = AppTheme.colors.black,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            bottom = AppTheme.dimensions.dialogContentBottomPadding
          ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
          items.forEach { item ->
            DropDownDialogElement(
              item = item,
              selectedItem = selectedItem,
              onClickItem = {
                onClickItem(it)
                onClickIconClose()
              },
            )
          }
        }
      }
    }
  }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
private fun DropDownDialogElement(
  item: String,
  selectedItem: String?,
  onClickItem: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (item == selectedItem) AppTheme.colors.secondary else Color.White)
      .clickable(interactionSource = MutableInteractionSource(),
        indication = ripple(color = AppTheme.colors.secondary),
        onClickLabel = null,
        onClick = { onClickItem(item) })
      .padding(AppTheme.dimensions.regularPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = item,
      style = AppTheme.typography.body2Regular,
      fontWeight = if (item == selectedItem) FontWeight.Medium else FontWeight.Normal,
      color = AppTheme.colors.black,
      textAlign = TextAlign.Start
    )
    if (item == selectedItem) {
      Spacer(modifier = Modifier.width(AppTheme.dimensions.smallPadding))
      CustomIcon(
        modifier = Modifier.padding(start = AppTheme.dimensions.mediumPadding),
        iconResId = R.drawable.common_ui_ic_check_mark,
        iconColor = AppTheme.colors.black,
        iconSize = IconSize.Small,
        contentDescription = "Chosen element", // TODO MOB-6712 update contentDescription
      )
    }
  }
}

@Preview
@Composable
fun DropDownDialogPreview() {
  DropDownDialog(
    modifier = Modifier,
    title = "Wybierz element",
    selectedItem = "Element 1",
    items = listOf("Element 1", "Element 2", "Element 3"),
    onClickIconClose = {},
    onClickItem = {},
  )
}