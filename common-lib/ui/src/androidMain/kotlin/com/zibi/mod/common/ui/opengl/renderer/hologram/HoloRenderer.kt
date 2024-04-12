package com.zibi.mod.common.ui.opengl.renderer.hologram

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Size
import com.zibi.mod.common.ui.opengl.renderer.IRenderer
import com.zibi.mod.common.ui.opengl.shader.Shader
import com.zibi.mod.common.ui.opengl.util.Accelerometer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class HoloRenderer(
  override val context: Context,
  override val shader: Shader,
  override val listBitmap: List<Bitmap>
) : IRenderer {

  private val accelerometer: Accelerometer = Accelerometer(context)
  private val drawer = Drawer()
  private var texProgram: Texture2dProgram? = null
  private var canvasSize: Size? = null


  override fun onSurfaceCreated(
    gl: GL10?,
    config: EGLConfig?
  ) {
    texProgram = Texture2dProgram(
      shader,
      accelerometer
    )
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
          listBitmap,
          false
        )
        drawable2d?.scaleSize = canvasSize as Size
      }
    }
  }

  override fun onDrawFrame(gl: GL10?) {
    gl?.apply {
      glClear(GLES20.GL_COLOR_BUFFER_BIT)
      glClearColor(
        1.0f,
        1.0f,
        1.0f,
        0.0f
      )
      drawer.draw(
        gl,
        canvasSize!!
      )
    }
  }

  override fun startAccelerometer(isPreview: Boolean) {
    accelerometer.init(isPreview)
  }

  override fun onResume() {
    accelerometer.registerListener()
  }

  override fun onPause() {
    accelerometer.unregisterListener()
  }

}



