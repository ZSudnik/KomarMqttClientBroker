package com.zibi.mod.common.ui.opengl.renderer.wave

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Size
import com.zibi.mod.common.ui.opengl.renderer.IRenderer
import com.zibi.mod.common.ui.opengl.shader.Shader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class WaveRenderer(
  override val context: Context,
  override val shader: Shader,
  override val listBitmap: List<Bitmap>
) : IRenderer {

  private var bitmap: Bitmap = listBitmap.first()
  private val drawer = Drawer()
  private var texProgram: Texture2dProgram? = null
  private var canvasSize: Size? = null


  override fun onSurfaceCreated(
    gl: GL10?,
    config: EGLConfig?
  ) {
    texProgram = Texture2dProgram(shader)
  }

  override fun onSurfaceChanged(
    gl: GL10?,
    width: Int,
    height: Int
  ) {
    gl?.apply {
      glViewport(
        0,
        0,
        width,
        height
      )
      canvasSize = Size(
        width,
        height
      )
      drawer.apply {
        init(
          texProgram = texProgram!!,
          bitmap,
          false
        )
        drawable2d?.scaleSize = canvasSize as Size
      }
    }
  }

  override fun onDrawFrame(gl: GL10?) {
    gl?.apply {
      glClearColor(
        0.1f,
        0.1f,
        0.1f,
        0.0f
      )
      glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT) // we're not using the stencil buffer now
      glEnable(GLES20.GL_DEPTH_TEST)
      drawer.draw(
        gl,
        canvasSize!!
      )
    }
  }

  override fun startAccelerometer(isPreview: Boolean) {}
  override fun onResume() {}
  override fun onPause() {}

}



