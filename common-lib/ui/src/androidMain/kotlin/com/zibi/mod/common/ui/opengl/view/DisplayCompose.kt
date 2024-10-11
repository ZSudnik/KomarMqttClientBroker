package com.zibi.mod.common.ui.opengl.view


import android.opengl.GLSurfaceView
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.zibi.mod.common.ui.opengl.TypeShader
import com.zibi.mod.common.ui.opengl.shader.ShaderFactory


@Composable
fun DisplayComposeGL(
  modifier: Modifier = Modifier,
  listResBitmap: List<Int>,
  typeShader: TypeShader,
  isPreview: Boolean = false
) {

  val context = LocalContext.current

  val renderer = remember {
    ShaderFactory.getRenderer(
      context,
      typeShader,
      listResBitmap
    )
  }

  Surface(
    modifier = modifier,
    color = Color.Transparent
  ) {
    var view: DrawGLSurfaceView? = remember { null }

    val lifeCycleState = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(key1 = lifeCycleState) {
      val observer = LifecycleEventObserver { _, event ->
        when (event) {
          Lifecycle.Event.ON_RESUME -> {
            view?.onResume()
            renderer.onResume()
          }
          Lifecycle.Event.ON_PAUSE -> {
            view?.onPause()
            renderer.onPause()
          }
          else -> {
          }
        }
      }
      lifeCycleState.addObserver(observer)

      onDispose {
        lifeCycleState.removeObserver(observer)
        view?.onPause()
        view = null
      }
    }

    AndroidView(modifier = modifier,
      factory = { context ->
        DrawGLSurfaceView(
          context,
          renderer,
          typeShader == TypeShader.WavingBitmap
        )
      }) { drawGLSurfaceView ->
      view = drawGLSurfaceView
//            if (BuildConfig.DEBUG)
      drawGLSurfaceView.debugFlags = GLSurfaceView.DEBUG_CHECK_GL_ERROR
      drawGLSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    } // start Accelerometer delay (or not if preview) because during in preview production error
    renderer.startAccelerometer(isPreview)
  }
}
