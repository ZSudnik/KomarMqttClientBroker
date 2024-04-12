package com.zibi.mod.common.ui.video

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Xml
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.PlayerView
import com.zibi.mod.common.ui.R

@Composable
fun VideoView(
  modifier: Modifier = Modifier,
  @RawRes source: Int,
  startWhenReady: Boolean = true,
  isLooping: Boolean = true,
  hideControls: Boolean = true,
  testMode: Boolean = false
) {
  if (testMode) {
    Box(modifier = modifier)
  } else {
    VideoContent(
      modifier = modifier,
      source = source,
      startWhenReady = startWhenReady,
      isLooping = isLooping,
      hideControls = hideControls
    )
  }
}

@SuppressLint("OpaqueUnitKey")
@OptIn(UnstableApi::class)
@Composable
private fun VideoContent(
  modifier: Modifier = Modifier,
  @RawRes source: Int,
  startWhenReady: Boolean,
  isLooping: Boolean,
  hideControls: Boolean
) {
  val context = LocalContext.current

  val exoPlayer = ExoPlayer.Builder(context)
    .build()
    .also { exoPlayer ->
      val mediaItem = MediaItem.Builder()
        .setUri(getSourceUri(context, source))
        .build()
      exoPlayer.apply {
        setMediaItem(mediaItem)
        if (isLooping) {
          repeatMode = Player.REPEAT_MODE_ALL
        }
        prepare()
        playWhenReady = startWhenReady
      }
    }
  DisposableEffect(
    AndroidView(
      modifier = modifier,
      factory = {
        PlayerView(context, getAttrs(context)).apply {
          if (hideControls) {
            hideController()
            useController = false
          }
          setShutterBackgroundColor(Color.TRANSPARENT)
          setKeepContentOnPlayerReset(true)
          resizeMode = RESIZE_MODE_FILL
          player = exoPlayer
        }
      }
    )
  ) {
    onDispose {
      exoPlayer.release()
    }
  }
}

private fun getSourceUri(context: Context, @RawRes source: Int) =
  Uri.parse(
    "android.resource://" + context.packageName + "/" + source
  )

private fun getAttrs(context: Context): AttributeSet? {
  val parser = context.resources.getXml(R.xml.player_texture_view)
  try {
    parser.next()
    parser.nextTag()
  } catch (e: Exception) {
    e.printStackTrace()
  }

  return Xml.asAttributeSet(parser)
}