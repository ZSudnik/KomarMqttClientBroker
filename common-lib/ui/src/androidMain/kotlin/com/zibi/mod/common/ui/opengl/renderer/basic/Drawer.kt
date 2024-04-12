package com.zibi.mod.common.ui.opengl.renderer.basic

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Size
import com.zibi.mod.common.ui.opengl.renderer.Drawable2d
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.createImageTexture
import com.zibi.mod.common.ui.opengl.util.use
import javax.microedition.khronos.opengles.GL10

class Drawer(private var bitmap: Bitmap? = null) {

  private val projectionMatrix = FloatArray(16)
  private var canvasSize: Size? = null

  private var texProgram: Texture2dProgram? = null
  private var texId: Int? = null

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
    texId = createImageTexture(
      bitmap.width,
      bitmap.height
    )
    drawable2d = Drawable2d(intArrayOf(texId!!))
    isInitialized = true
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
        texId!!
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

    drawBitmap(
      gl,
      projectionMatrix
    )
  }

  private fun drawBitmap(
    gl: GL10,
    projection: FloatArray
  ) {
    checkGlError("drawBitmap start")
    gl.use(GLES20.GL_BLEND) {
      glBlendFunc(
        GLES20.GL_ONE,
        GLES20.GL_ONE_MINUS_SRC_ALPHA
      )
      texProgram?.let {
        drawable2d!!.draw(
          it,
          projection,
          null
        )
      }
    }
    checkGlError("drawBitmap end")
  }
}

