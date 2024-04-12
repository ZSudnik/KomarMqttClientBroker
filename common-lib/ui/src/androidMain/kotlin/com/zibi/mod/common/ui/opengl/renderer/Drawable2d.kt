package com.zibi.mod.common.ui.opengl.renderer

import android.opengl.Matrix
import android.util.Size
import com.zibi.mod.common.ui.opengl.util.IDENTITY_MATRIX
import com.zibi.mod.common.ui.opengl.util.SIZEOF_FLOAT
import com.zibi.mod.common.ui.opengl.util.createFloatBuffer
import java.nio.FloatBuffer

/**
 * Base class for stuff we like to draw.
 */
class Drawable2d(private val texId: IntArray) {

  /**
   * Returns the array of vertices.
   * To avoid allocations, this returns internal state.  The caller must not modify it.
   */
  private var vertexArray: FloatBuffer = RECTANGLE_BUF

  /**
   * Returns the array of texture coordinates.
   * To avoid allocations, this returns internal state.  The caller must not modify it.
   */
  private var texCoordArray: FloatBuffer = RECTANGLE_TEX_BUF

  /**
   * Returns the number of position coordinates per vertex.  This will be 2 or 3.
   */
  private var coordsPerVertex = 2

  /**
   * Returns the width, in bytes, of the data for each vertex.
   */
  private val vertexStride = coordsPerVertex * SIZEOF_FLOAT

  /**
   * Returns the width, in bytes, of the data for each texture coordinate.
   */
  private val texCoordStride: Int = 2 * SIZEOF_FLOAT

  /**
   * Returns the number of vertices stored in the vertex array.
   */
  private var vertexCount = RECTANGLE_COORDS.size / coordsPerVertex

  companion object {
    /**
     * Simple square, specified as a triangle strip.  The square is centered on (0,0) and has
     * a size of 1x1.
     * Triangles are 0-1-2 and 2-1-3 (counter-clockwise winding).
     */
    private val RECTANGLE_COORDS = floatArrayOf(
      -0.5f,
      -0.5f, // 0 bottom left
      0.5f,
      -0.5f, // 1 bottom right
      -0.5f,
      0.5f, // 2 top left
      0.5f,
      0.5f
    )
    private val RECTANGLE_TEX_COORDS = floatArrayOf(
      0.0f,
      1.0f, // 0 bottom left
      1.0f,
      1.0f, // 1 bottom right
      0.0f,
      0.0f, // 2 top left
      1.0f,
      0.0f // 3 top right
    )
    private val RECTANGLE_BUF = createFloatBuffer(RECTANGLE_COORDS)
    private val RECTANGLE_TEX_BUF = createFloatBuffer(RECTANGLE_TEX_COORDS)
  }

  /**
   * Returns the sprite scale along the XY axis.
   */
  var scaleSize = Size(
    0,
    0
  )
    set(value) {
      mMatrixNoReady = true
      field = value
      Matrix.setIdentityM(
        mvMatrix,
        0
      ) //set position
      Matrix.translateM(
        mvMatrix,
        0,
        value.width.toFloat() / 2,
        value.height.toFloat() / 2,
        0.0f
      )
      Matrix.scaleM(
        mvMatrix,
        0,
        value.width.toFloat(),
        value.height.toFloat(),
        1.0f
      )
      mMatrixNoReady = false
    }

  private var mMatrixNoReady: Boolean = false
  private val mvpMatrix = FloatArray(16)

  /**
   * Returns the model-view matrix.
   * To avoid allocations, this returns internal state.  The caller must not modify it.
   */
  private val mvMatrix: FloatArray = FloatArray(16) { 0f }


  /**
   * Draws the rectangle with the supplied program and projection matrix.
   */
  fun draw(
    program: ITexture2dProgram,
    projectionMatrix: FloatArray?,
    distance: Float?
  ) {
    if (mMatrixNoReady) return
    Matrix.multiplyMM(
      mvpMatrix,
      0,
      projectionMatrix,
      0,
      mvMatrix,
      0
    )

    program.draw(
      mvpMatrix,
      this.vertexArray,
      0,
      this.vertexCount,
      this.coordsPerVertex,
      this.vertexStride,
      IDENTITY_MATRIX,
      texCoordArray,
      texId,
      texCoordStride,
      distance
    )
  }

}