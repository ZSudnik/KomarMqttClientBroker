package com.zibi.mod.common.ui.opengl.renderer.wave

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Size
import com.zibi.mod.common.ui.opengl.renderer.Drawable2d
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.use

import javax.microedition.khronos.opengles.GL10

class Drawer(private var bitmap: Bitmap? = null) {

  private val projectionMatrix = FloatArray(16)
  private var canvasSize: Size? = null

  private var texProgram: Texture2dProgram? = null
  private var texId: Int = -1

  private var isInitialized = false
  private var updateTexture = false
  private var recycleBitmap = false
  var drawable2d: Drawable2d? = null


  fun init(
    texProgram: Texture2dProgram,
    bitmap: Bitmap,
    recycle: Boolean = false
  ) {
    this.texProgram = texProgram
    texId = texProgram.createTexture(
      bitmap.width,
      bitmap.height
    )
    drawable2d = Drawable2d(intArrayOf(texId))
    isInitialized = true // bitmap
    this.bitmap = bitmap
    updateTexture = true
    this.recycleBitmap = recycle

  }

  private fun loadTexture(
    gl: GL10,
    bitmap: Bitmap
  ) {
    gl.apply {
      glActiveTexture(GLES20.GL_TEXTURE0)
      glBindTexture(
        GLES20.GL_TEXTURE_2D,
        texId
      )
      GLUtils.texImage2D(
        GLES20.GL_TEXTURE_2D,
        0,
        bitmap,
        0
      )
      glBindTexture(
        GLES20.GL_TEXTURE_2D,
        0
      )
    }
  }

  private var timeLast = System.currentTimeMillis()
  private var timeNow = 0L
  private var delta = 0L
  private var speed = 1
  private var uTime = 0f

  fun draw(
    gl: GL10,
    canvasSize: Size
  ) {
    if (!isInitialized) return //"Square is not initialized"
    if (bitmap != null && updateTexture) {
      loadTexture(
        gl,
        bitmap!!
      )
      if (recycleBitmap) {
        bitmap!!.recycle()
        bitmap = null
      }
      updateTexture = false
    }
    if (this.canvasSize != canvasSize) {
      this.canvasSize = canvasSize
      Matrix.orthoM(
        projectionMatrix,
        0,
        0f,
        canvasSize.width.toFloat(),
        0f,
        canvasSize.height.toFloat(),
        -1f,
        1f
      )
    }

    timeNow = System.currentTimeMillis()
    delta = timeNow - timeLast
    timeLast = timeNow
    uTime += (delta * 0.001 * speed).toFloat()

    drawBitmap(
      gl,
      projectionMatrix,
      uTime.mod(PI_2)
    )
  }

  private fun drawBitmap(
    gl: GL10,
    projection: FloatArray,
    distance: Float
  ) {
    checkGlError("drawBitmap start")
    gl.use(GLES20.GL_BLEND) {
      glBlendFunc(
        GLES20.GL_SRC_ALPHA,
        GLES20.GL_ONE_MINUS_SRC_ALPHA
      )
      texProgram?.let {
        drawable2d!!.draw(
          it,
          projection,
          distance
        )
      }
    }
    checkGlError("drawBitmap end")
  }

  companion object {
    private val PI_2: Float = 2f * Math.PI.toFloat()
  }
}

