package com.zibi.mod.common.ui.opengl.renderer.basic

import android.opengl.GLES20
import com.zibi.mod.common.ui.opengl.renderer.ITexture2dProgram
import com.zibi.mod.common.ui.opengl.shader.Shader
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.checkLocation
import com.zibi.mod.common.ui.opengl.util.createProgram
import java.nio.FloatBuffer

/**
 * GL program and supporting functions for textured 2D shapes.
 */
class Texture2dProgram(shader: Shader) : ITexture2dProgram {

  // Handles to the GL program and various components of it.
  private var mProgramHandle: Int
  private val muMVPMatrixLoc: Int
  private val muTexMatrixLoc: Int
  private var muKernelLoc: Int
  private var muTexOffsetLoc = 0
  private var muColorAdjustLoc = 0
  private val maPositionLoc: Int
  private val maTextureCoordLoc: Int
  private val uTime: Int = -1
  private val mKernel = FloatArray(KERNEL_SIZE)
  private lateinit var mTexOffset: FloatArray
  private var mColorAdjust = 0f

  /**
   * Prepares the program in the current EGL context.
   */
  init {
    mProgramHandle = createProgram(
      shader.vertexShader,
      shader.fragmentShader
    )
    if (mProgramHandle == 0) {
      throw RuntimeException("Unable to create program")
    }

    // get locations of attributes and uniforms
    maPositionLoc = GLES20.glGetAttribLocation(
      mProgramHandle,
      "a_VertexPosition"
    )
    checkLocation(
      maPositionLoc,
      "a_VertexPosition"
    )
    maTextureCoordLoc = GLES20.glGetAttribLocation(
      mProgramHandle,
      "a_TextureCoordinates"
    )
    checkLocation(
      maTextureCoordLoc,
      "a_TextureCoordinates"
    )
    muMVPMatrixLoc = GLES20.glGetUniformLocation(
      mProgramHandle,
      "uMVPMatrix"
    )
    checkLocation(
      muMVPMatrixLoc,
      "uMVPMatrix"
    )
    muTexMatrixLoc = GLES20.glGetUniformLocation(
      mProgramHandle,
      "uTexMatrix"
    )
    checkLocation(
      muTexMatrixLoc,
      "uTexMatrix"
    )

    muKernelLoc = GLES20.glGetUniformLocation(
      mProgramHandle,
      "uKernel"
    )
    if (muKernelLoc < 0) { // no kernel in this one
      muKernelLoc = -1
      muTexOffsetLoc = -1
      muColorAdjustLoc = -1
    } else { // has kernel, must also have tex offset and color adj
      muTexOffsetLoc = GLES20.glGetUniformLocation(
        mProgramHandle,
        "uTexOffset"
      )
      checkLocation(
        muTexOffsetLoc,
        "uTexOffset"
      )
      muColorAdjustLoc = GLES20.glGetUniformLocation(
        mProgramHandle,
        "uColorAdjust"
      )
      checkLocation(
        muColorAdjustLoc,
        "uColorAdjust"
      )

      // initialize default values
      setKernel(
        floatArrayOf(
          0f,
          0f,
          0f,
          0f,
          1f,
          0f,
          0f,
          0f,
          0f
        ),
        0f
      )
      setTexSize(
        256,
        256
      )
    }
  }

  /**
   * Releases the program.
   * The appropriate EGL context must be current (i.e. the one that was used to create
   * the program).
   */
  fun release() {
    GLES20.glDeleteProgram(mProgramHandle)
    mProgramHandle = -1
  }

  /**
   * Creates a texture object suitable for use with this program.
   * On exit, the texture will be bound.
   */
  fun createTextureObject(): Int {
    val textures = IntArray(1)
    GLES20.glGenTextures(
      1,
      textures,
      0
    )
    checkGlError("glGenTextures")
    val texId = textures[0]
    GLES20.glBindTexture(
      GLES20.GL_TEXTURE_2D,
      texId
    )
    checkGlError("glBindTexture $texId")
    GLES20.glTexParameterf(
      GLES20.GL_TEXTURE_2D,
      GLES20.GL_TEXTURE_MIN_FILTER,
      GLES20.GL_NEAREST.toFloat()
    )
    GLES20.glTexParameterf(
      GLES20.GL_TEXTURE_2D,
      GLES20.GL_TEXTURE_MAG_FILTER,
      GLES20.GL_LINEAR.toFloat()
    )
    GLES20.glTexParameteri(
      GLES20.GL_TEXTURE_2D,
      GLES20.GL_TEXTURE_WRAP_S,
      GLES20.GL_CLAMP_TO_EDGE
    )
    GLES20.glTexParameteri(
      GLES20.GL_TEXTURE_2D,
      GLES20.GL_TEXTURE_WRAP_T,
      GLES20.GL_CLAMP_TO_EDGE
    )
    checkGlError("glTexParameter")
    return texId
  }

  /**
   * Configures the convolution filter values.
   * @param values Normalized filter values; must be KERNEL_SIZE elements.
   */
  fun setKernel(
    values: FloatArray,
    colorAdj: Float
  ) {
    require(values.size == KERNEL_SIZE) {
      "Kernel size is " + values.size + " vs. " + KERNEL_SIZE
    }
    System.arraycopy(
      values,
      0,
      mKernel,
      0,
      KERNEL_SIZE
    )
    mColorAdjust = colorAdj
  }

  /**
   * Sets the size of the texture.  This is used to find adjacent texels when filtering.
   */
  private fun setTexSize(
    width: Int,
    height: Int
  ) {
    val rw = 1.0f / width
    val rh = 1.0f / height

    // Don't need to create a new array here, but it's syntactically convenient.
    mTexOffset = floatArrayOf(
      -rw,
      -rh,
      0f,
      -rh,
      rw,
      -rh,
      -rw,
      0f,
      0f,
      0f,
      rw,
      0f,
      -rw,
      rh,
      0f,
      rh,
      rw,
      rh
    )
  }

  /**
   * Issues the draw call.  Does the full setup on every call.
   *
   * @param mvpMatrix       The 4x4 projection matrix.
   * @param vertexBuffer    Buffer with vertex position data.
   * @param firstVertex     Index of first vertex to use in vertexBuffer.
   * @param vertexCount     Number of vertices in vertexBuffer.
   * @param coordsPerVertex The number of coordinates per vertex (e.g. x,y is 2).
   * @param vertexStride    Width, in bytes, of the position data for each vertex (often
   * vertexCount * sizeof(float)).
   * @param texMatrix       A 4x4 transformation matrix for texture coords.  (Primarily intended
   * for use with SurfaceTexture.)
   * @param texBuffer       Buffer with vertex texture data.
   * @param texStride       Width, in bytes, of the texture data for each vertex.
   */
  override fun draw(
    mvpMatrix: FloatArray?,
    vertexBuffer: FloatBuffer?,
    firstVertex: Int,
    vertexCount: Int,
    coordsPerVertex: Int,
    vertexStride: Int,
    texMatrix: FloatArray?,
    texBuffer: FloatBuffer?,
    texIdArray: IntArray,
    texStride: Int?,
    time: Float?
  ) {

    checkGlError("draw start")

    // Select the program.
    GLES20.glUseProgram(mProgramHandle)
    checkGlError("glUseProgram")

    // Set the texture.
    for (i in texIdArray.indices) {
      GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
      GLES20.glBindTexture(
        GLES20.GL_TEXTURE_2D,
        texIdArray[i]
      )
    }

    // Copy the model / view / projection matrix over.
    GLES20.glUniformMatrix4fv(
      muMVPMatrixLoc,
      1,
      false,
      mvpMatrix,
      0
    )
    checkGlError("glUniformMatrix4fv")

    // Copy the texture transformation matrix over.
    GLES20.glUniformMatrix4fv(
      muTexMatrixLoc,
      1,
      false,
      texMatrix,
      0
    )
    checkGlError("glUniformMatrix4fv")

    // Enable the "a_VertexPosition" vertex attribute.
    GLES20.glEnableVertexAttribArray(maPositionLoc)
    checkGlError("glEnableVertexAttribArray")

    // Connect vertexBuffer to "a_VertexPosition".
    GLES20.glVertexAttribPointer(
      maPositionLoc,
      coordsPerVertex,
      GLES20.GL_FLOAT,
      false,
      vertexStride,
      vertexBuffer
    )
    checkGlError("glVertexAttribPointer")

    if (time != null) {
      GLES20.glUniform1f(
        uTime,
        time
      )
      checkGlError("glUniform1f")
    }

    // Enable the "a_TextureCoordinates" vertex attribute.
    GLES20.glEnableVertexAttribArray(maTextureCoordLoc)
    checkGlError("glEnableVertexAttribArray")

    // Connect texBuffer to "a_TextureCoordinates".
    if (texStride != null) {
      GLES20.glVertexAttribPointer(
        maTextureCoordLoc,
        2,
        GLES20.GL_FLOAT,
        false,
        texStride,
        texBuffer
      )
      checkGlError("glVertexAttribPointer")
    }

    // Populate the convolution kernel, if present.
    if (muKernelLoc >= 0) {
      GLES20.glUniform1fv(
        muKernelLoc,
        KERNEL_SIZE,
        mKernel,
        0
      )
      GLES20.glUniform2fv(
        muTexOffsetLoc,
        KERNEL_SIZE,
        mTexOffset,
        0
      )
      GLES20.glUniform1f(
        muColorAdjustLoc,
        mColorAdjust
      )
    }

    // Draw the rect.
    GLES20.glDrawArrays(
      GLES20.GL_TRIANGLE_STRIP,
      firstVertex,
      vertexCount
    )
    checkGlError("glDrawArrays")

    // Done -- disable vertex array, texture, and program.
    GLES20.glDisableVertexAttribArray(maPositionLoc)
    GLES20.glDisableVertexAttribArray(maTextureCoordLoc)
    GLES20.glBindTexture(
      GLES20.GL_TEXTURE_2D,
      0
    )
    GLES20.glUseProgram(0)
  }

  companion object {
    private const val KERNEL_SIZE = 9
  }
}