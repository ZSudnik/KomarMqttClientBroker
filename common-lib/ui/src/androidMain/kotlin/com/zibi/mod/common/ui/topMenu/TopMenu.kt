package com.zibi.mod.common.ui.topMenu

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.model.TopMenuTitleSpanData
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun TopMenu(
  title: String? = null,
  titleSpanData: TopMenuTitleSpanData? = null,
  iconTitleResId: Int? = null,
  iconMainResId: Int? = null,
  iconMenuResId: Int? = null,
  progress: Int? = null,
  backgroundColor: Color = AppTheme.colors.background,
  onIconMainClick: (() -> Unit)? = null,
  onIconMenuClick: (() -> Unit)? = null,
  isIconMenuRotate: Boolean = false,
  iconMenuColorActive: Color = AppTheme.colors.black,
  iconMenuColorPassive: Color = AppTheme.colors.black,
) {

  TopAppBar(
    backgroundColor = backgroundColor,
    elevation = AppTheme.dimensions.zero,
    contentPadding = PaddingValues(
      AppTheme.dimensions.mediumPadding, AppTheme.dimensions.smallPadding
    )
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier.size(48.dp),
      ) {
        if (iconMainResId != null) {
          IconButton(interactionSource = NoRippleInteractionSource(), modifier = Modifier.size(48.dp), onClick = {
            onIconMainClick?.let { it() }
          }) {
            CustomIcon(
              iconResId = iconMainResId,
              iconSize = IconSize.Medium,
            )
          }
        }
      }

      if (title != null) {
        Text(
          modifier = Modifier
            .padding(
              start = AppTheme.dimensions.mediumPadding,
              end = AppTheme.dimensions.mediumPadding,
            )
            .weight(1F),
          text = titleSpanData?.let {
            buildAnnotatedString {
              if (it.range.last >= title.length || it.range.isEmpty())
                append(title)
              else {
                append(title.substring(0, it.range.first))
                withStyle(style = SpanStyle(color = it.color)) {
                  append(title.subSequence(it.range))
                }
                if (it.range.last + 1 < title.length)
                  append(title.substring(it.range.last + 1))
              }
            }
          } ?: AnnotatedString(title),
          style = AppTheme.typography.subtitle,
          textAlign = TextAlign.Center,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = AppTheme.colors.black
        )
      } else {
        if (iconTitleResId != null) {
          Image(
            modifier = Modifier.fillMaxHeight(1f)
              .padding(
                start = AppTheme.dimensions.mediumPadding,
                end = AppTheme.dimensions.mediumPadding,
              )
              .weight(1F),
            painter = painterResource(id = iconTitleResId),
            contentDescription = null,
          )
        }
      }
      Box(
        modifier = Modifier.size(48.dp),
      ) {
        if (iconMenuResId != null) {
          var angle = 0f
          if(isIconMenuRotate){
            val infiniteTransition = rememberInfiniteTransition(label = "")
            angle = infiniteTransition.animateFloat(
              initialValue = 0f,
              targetValue = 360f,
              animationSpec = infiniteRepeatable(
                animation = keyframes {
                  durationMillis = 1000
                }
              ), label = ""
            ).value
          }
          IconButton(
            interactionSource = NoRippleInteractionSource(),
            modifier = Modifier.size(48.dp),
            onClick = { onIconMenuClick?.let { it() } }
          ) {
            CustomIcon(
              modifier = Modifier.graphicsLayer {
                rotationZ = angle
              },
              iconResId = iconMenuResId,
              contentDescription = null,
              iconSize = IconSize.Medium,
              iconColor = if(isIconMenuRotate) iconMenuColorActive else iconMenuColorPassive,
            )
          }
        }
      }
    }
  }
  if (progress != null) {
    val floatProgress = (progress.toDouble() / 100.00).toFloat()
    val progressMsg =
      StringBuilder().append(progress).append(stringResource(id = R.string.common_ui_top_menu_progress_percentage)).toString()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(AppTheme.colors.background)
        .padding(
          start = AppTheme.dimensions.regularPadding,
          top = AppTheme.dimensions.zero,
          end = AppTheme.dimensions.regularPadding,
          bottom = AppTheme.dimensions.regularPadding
        ),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          modifier = Modifier.fillMaxWidth(floatProgress),
          text = progressMsg,
          style = AppTheme.typography.label2Regular,
          textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
        LinearProgressIndicator(
          progress = floatProgress,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimensions.progressBarIndicatorRadius)),
          color = AppTheme.colors.progressIndicator,
          backgroundColor = AppTheme.colors.progressBackground
        )
      }
    }
  }
}