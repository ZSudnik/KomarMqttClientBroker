package com.zibi.mod.common.ui.document.maincard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import com.zibi.mod.common.ui.model.KeyValueData
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.OnLifecycleEvent
import com.zibi.mod.common.ui.video.VideoView
import com.zibi.mod.common.ui.R

@Composable
fun DocumentMainCard(
  modifier: Modifier = Modifier,
  giloshBackground: Int,
  userPhoto: Bitmap,
  keyValueItems: List<KeyValueData>,
  testMode: Boolean = false,
) {
//  var view: MainCardBackgroundView? = remember { null }
//  val backgroundViewThreadHandler = remember { GLThreadHandler(true) }
//  val smallPadding = AppTheme.dimensions.smallPadding
  val xxMediumPadding = AppTheme.dimensions.xxMediumPadding
  val regularPadding = AppTheme.dimensions.regularPadding
  val xRegularPadding = AppTheme.dimensions.xRegularPadding
  val mainCardHeight = 440.dp
  val imageContainerWidth = 125.dp
  val imageContainerHeight = 167.dp
  val validityContainerHeight = 76.dp
//  val validityIconSize = 36.dp
//  val splitOdlValue = " "
//  val splitNewValue = "\n"

  OnLifecycleEvent { _, event ->
    when (event) {
      Lifecycle.Event.ON_RESUME -> {
//        view?.onResume()
//        backgroundViewThreadHandler.startThread()
      }
      Lifecycle.Event.ON_PAUSE -> {
//        view?.onPause()
//        backgroundViewThreadHandler.stopThread()
      }
      else -> {}
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(mainCardHeight),
    backgroundColor = Color.White,
    shape = RoundedCornerShape(AppTheme.dimensions.xxMediumPadding),
    elevation = AppTheme.dimensions.xxMediumPadding,
  ) {
    Box() {
      Box(modifier = Modifier.fillMaxSize()) {
        Image(
          painter = painterResource(id = giloshBackground),
          contentDescription = null,
          contentScale = ContentScale.FillBounds,
        )

//        if (!testMode) {
//          AndroidView(
//            factory = { context ->
//              MainCardBackgroundView(context, giloshForeground).also {
//                backgroundViewThreadHandler.setGlView(it)
//                view = it
//              }
//            }
//          )
//        }
      }

      Box(
        modifier = Modifier
          .fillMaxSize()
      ) {
        ConstraintLayout(
          modifier = Modifier.fillMaxSize()
        ) {

          val (photoRef, flagRef, hologramRef, cardItemsRef, bottomBgRef) = createRefs()

          Card(
            modifier = Modifier
              .width(imageContainerWidth)
              .height(imageContainerHeight)
              .constrainAs(photoRef) {
                top.linkTo(parent.top, margin = xRegularPadding)
                start.linkTo(parent.start, margin = xRegularPadding)
              },
            backgroundColor = Color.White,
            shape = RoundedCornerShape(AppTheme.dimensions.xxMediumPadding),
            elevation = AppTheme.dimensions.zero,
          ) {

            Image(
              modifier = Modifier
                .fillMaxSize(),
              bitmap = userPhoto.asImageBitmap(),
              contentDescription = null
            )
          }

          Box(
            modifier = Modifier
              .constrainAs(flagRef) {
                top.linkTo(photoRef.bottom, margin = regularPadding)
                start.linkTo(parent.start, margin = regularPadding)
              }
          ) {
            VideoView(
              modifier = Modifier
                .width(AppTheme.dimensions.localDocumentFlagWidth)
                .height(AppTheme.dimensions.localDocumentFlagHeight),
              source = R.raw.common_ui_waving_polish_flag,
              testMode = testMode
            )
          }

          Box(
            modifier = Modifier
              .constrainAs(hologramRef) {
                top.linkTo(flagRef.bottom, margin = regularPadding)
                start.linkTo(parent.start, margin = regularPadding)
              },
          ) {

          }

//          Text(
//            modifier = Modifier
//              .constrainAs(hologramTitleRef) {
//                top.linkTo(hologramRef.top)
//                bottom.linkTo(hologramRef.bottom)
//                start.linkTo(hologramRef.end, margin = smallPadding)
//              },
////            text = stringResource(id = R.string.republic_of_poland)
////              .replace(splitOdlValue, splitNewValue),
////            style = AppTheme.typography.labelRegularLight
//          )

          Column(
            modifier = Modifier
              .constrainAs(cardItemsRef) {
                top.linkTo(photoRef.top)
                start.linkTo(photoRef.end, margin = xRegularPadding)
                end.linkTo(parent.end, margin = xRegularPadding)
                width = Dimension.fillToConstraints
              }
          ) {
            keyValueItems.forEach { documentCardItem ->
              Text(
                text = documentCardItem.title,
                style = AppTheme.typography.bodyMedium,
              )
              Text(
                text = documentCardItem.description,
                style = AppTheme.typography.body2Regular,
              )
              if (keyValueItems.lastIndex
                != keyValueItems.indexOf(documentCardItem)
              ) {
                Spacer(modifier = Modifier.height(xxMediumPadding))
              }
            }
          }

          Box(
            modifier = Modifier
              .background(Color.White)
              .height(validityContainerHeight)
              .padding(xRegularPadding)
              .constrainAs(bottomBgRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
              }
          ) {
//            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
//              val (validityIconRef, validityInfoRef) = createRefs()
//
////              Icon(
////                modifier = Modifier
////                  .size(validityIconSize)
////                  .constrainAs(validityIconRef) {
////                    top.linkTo(parent.top)
////                    bottom.linkTo(parent.bottom)
////                    start.linkTo(parent.start)
////                  },
////                painter = painterResource(
////                  id = if (isValid) R.drawable.valid else R.drawable.invalid
////                ),
//                contentDescription = null,
//                tint = if (isValid)
//                  AppTheme.colors.green900
//                else
//                  AppTheme.colors.red900,
//              )
//
////              val validityMessage = if (isValid)
////                R.string.document_is_valid
////              else
////                R.string.document_is_invalid
//              Text(
//                modifier = Modifier.fillMaxWidth().constrainAs(validityInfoRef) {
//                  top.linkTo(validityIconRef.top)
//                  bottom.linkTo(validityIconRef.bottom)
//                  start.linkTo(validityIconRef.end, margin = regularPadding)
//                  end.linkTo(parent.end, margin = xRegularPadding)
//                  width = Dimension.fillToConstraints
//                },
//                text = stringResource(id = validityMessage),
//                textAlign = TextAlign.Start,
//                style = AppTheme.typography.body2Regular,
//                color = if (isValid)
//                  AppTheme.colors.green900
//                else
//                  AppTheme.colors.red900,
//              )
//            }
          }
        }
      }
    }
  }
}