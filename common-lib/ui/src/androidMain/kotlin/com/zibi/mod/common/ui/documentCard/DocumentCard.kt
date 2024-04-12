package com.zibi.mod.common.ui.documentCard

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.status.Status
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocumentCard(
  modifier: Modifier = Modifier,
  item: DocumentCardItem,
): @Composable() (PagerScope.(Int) -> Unit) {
  val smallPadding = AppTheme.dimensions.smallPadding
  val mediumPadding = AppTheme.dimensions.mediumPadding
  val xxMediumPadding = AppTheme.dimensions.xxMediumPadding
  val regularPadding = AppTheme.dimensions.regularPadding
  val iconRoundedCornerShapePercentage = 50

  Card(
    modifier = modifier
      .width(AppTheme.dimensions.documentCardWidth)
      .height(AppTheme.dimensions.documentCardHeight)
      .clickable(interactionSource = MutableInteractionSource(),
        indication = null,
        onClickLabel = null,
        onClick = { item.onClick() }),
    backgroundColor = Color.White,
    shape = RoundedCornerShape(AppTheme.dimensions.cardRadius),
    elevation = AppTheme.dimensions.documentCardElevation
  ) {
    ConstraintLayout {
      val (iconRef, logoRef, titleRef, buttonRef, bottomBarRef, statusRef) = createRefs()

      IconButton(
        modifier = Modifier
          .constrainAs(iconRef) {
            top.linkTo(parent.top, margin = regularPadding)
            start.linkTo(parent.start, margin = regularPadding)
          }
          .background(
            color = item.secondaryColor,
            shape = RoundedCornerShape(iconRoundedCornerShapePercentage)
          ),
        onClick = {},
        enabled = false
      ) {
        CustomIcon(
          iconResId = item.iconResId,
          iconColor = item.primaryColor,
          contentDescription = null,
        )
      }

      Box(
        modifier = Modifier
          .width(AppTheme.dimensions.documentCardLogoWidth)
          .height(AppTheme.dimensions.documentCardLogoHeight)
          .constrainAs(logoRef) {
            end.linkTo(parent.end, margin = regularPadding)
            top.linkTo(iconRef.bottom, margin = -(regularPadding))
          }, contentAlignment = Alignment.Center
      ) {
        if (item.logoResId != null) {
          Image(
            painter = painterResource(id = item.logoResId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppTheme.colors.grey),
          )
        }
      }

      item.status?.let { status ->
        Box(
          modifier = Modifier.constrainAs(statusRef) {
            top.linkTo(
              iconRef.top, margin = -(smallPadding)
            )
            end.linkTo(
              parent.end, margin = regularPadding
            )
          },
        ) {
          Status(
            text = status.text,
            type = status.type,
            iconResId = status.iconResId
          )
        }
      }

      Text(
        modifier = Modifier.constrainAs(titleRef) {
          start.linkTo(
            parent.start, margin = regularPadding
          )
          end.linkTo(
            logoRef.start, margin = -(mediumPadding)
          )
          bottom.linkTo(
            bottomBarRef.top, margin = xxMediumPadding
          )
          width = Dimension.fillToConstraints
        },
        text = item.title,
        style = AppTheme.typography.subtitle,
        overflow = TextOverflow.Ellipsis,
        maxLines = 3
      )

      Row(
        modifier = Modifier.constrainAs(buttonRef) {
          end.linkTo(
            parent.end, margin = regularPadding
          )
          bottom.linkTo(
            bottomBarRef.top, xxMediumPadding
          )
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = item.showLabel,
          style = AppTheme.typography.body2Regular,
          fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.width(AppTheme.dimensions.smallPadding))
        CustomIcon(
          iconResId = R.drawable.common_ui_ic_chevron_right_bold,
          iconSize = IconSize.MSmall,
          contentDescription = null,
        )
      }

      Box(modifier = Modifier
        .constrainAs(bottomBarRef) {
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          bottom.linkTo(parent.bottom)
        }
        .fillMaxWidth()
        .height(AppTheme.dimensions.mediumPadding)
        .background(
          brush = Brush.horizontalGradient(
            colors = listOf(
              item.primaryColor, item.secondaryColor
            )
          )
        )) {
        Image(
          painter = painterResource(id = item.bottomBarResId),
          contentDescription = null,
          contentScale = ContentScale.FillBounds
        )
      }
    }
  }
  return {}
}


//@Preview
//@Composable
//fun DocumentCardPreview() {
//  DocumentCard(
//    item = DocumentCardItem(
//      title = stringResource(R.string.coi_common_ui_document_card_title_id),
//      showLabel = stringResource(id = R.string.coi_common_ui_document_card_button_text),
//      primaryColor = AppTheme.colors.documentCardId,
//      secondaryColor = AppTheme.colors.documentCardId.copy(alpha = 0.1f),
//      iconResId = R.drawable.coi_common_ui_ic_document_id,
//      logoResId = R.drawable.coi_common_ui_logo_document_id,
//      bottomBarResId = R.drawable.gradient_document_card_id,
//      onClick = {}
//    )
//  )
//}