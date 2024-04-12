package com.zibi.mod.common.ui.opengl.renderer.hologram

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Size
import com.zibi.mod.common.ui.opengl.renderer.Drawable2d
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.use

import javax.microedition.khronos.opengles.GL10

class Drawer(private var listBitmaps: List<Bitmap>? = null) {

  private val projectionMatrix = FloatArray(16)
  private var canvasSize: Size? = null

  private var texProgram: Texture2dProgram? = null
  private var texIdArray: IntArray? = null

  private var isInitialized = false
  private var updateTexture = false
  private var recycleBitmap = false
  var drawable2d: Drawable2d? = null


  fun init(
    texProgram: Texture2dProgram,
    listBitmap: List<Bitmap>,
    recycle: Boolean = false
  ) {
    this.texProgram = texProgram
    texIdArray = texProgram.createTexture(listBitmap)
    drawable2d = Drawable2d(texIdArray!!)
    isInitialized = true
    this.listBitmaps = listBitmap
    updateTexture = true
    this.recycleBitmap = recycle
  }

  private fun loadTexture(
    gl: GL10,
    bitmap: Bitmap,
    index: Int
  ) {
    gl.apply {
      glActiveTexture(GLES20.GL_TEXTURE0 + index)
      glBindTexture(
        GLES20.GL_TEXTURE_2D,
        texIdArray!![index]
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
    if (listBitmaps != null && updateTexture) {
      for (i in listBitmaps!!.indices) {
        loadTexture(
          gl,
          listBitmaps!![i],
          i
        )
        if (recycleBitmap) {
          listBitmaps!![i].recycle() //                    listBitmaps!![i] = null
        }
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

