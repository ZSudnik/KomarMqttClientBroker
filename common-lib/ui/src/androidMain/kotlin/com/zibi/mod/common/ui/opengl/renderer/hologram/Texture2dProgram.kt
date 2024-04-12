package com.zibi.mod.common.ui.opengl.renderer.hologram

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES30
import com.zibi.mod.common.ui.opengl.renderer.ITexture2dProgram
import com.zibi.mod.common.ui.opengl.shader.Shader
import com.zibi.mod.common.ui.opengl.util.Accelerometer
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.checkLocation
import com.zibi.mod.common.ui.opengl.util.createProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


/**
 * GL program and supporting functions for textured 2D shapes.
 */
class Texture2dProgram(
  private val shader: Shader,
  private val accelerometer: Accelerometer
) : ITexture2dProgram {

  // Handles to the GL program and various components of it.
  private var mProgramHandle: Int
  private val muMVPMatrixLoc: Int
  private val muTexMatrixLoc: Int
  private val maPositionLoc: Int
  private val maTextureCoordLoc: Int
  private val uAccelerometerCoordinates: Int

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
    uAccelerometerCoordinates = GLES20.glGetUniformLocation(
      mProgramHandle,
      "u_AccelerometerCoordinates"
    )
    checkLocation(
      uAccelerometerCoordinates,
      "u_AccelerometerCoordinates"
    )

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
  fun createTexture(listBitmap: List<Bitmap>): IntArray {
    val manyTex = listBitmap.size
    val textures = IntArray(manyTex)
    GLES20.glGenTextures(
      manyTex,
      textures,
      0
    )
    checkGlError("glGenTextures")

    for (i in listBitmap.indices) {
      GLES20.glActiveTexture(GL10.GL_TEXTURE0 + i)
      GLES20.glBindTexture(
        GLES20.GL_TEXTURE_2D,
        textures[i]
      )
      checkGlError("glBindTexture ${textures[i]}")

      GLES20.glTexParameterf(
        GLES20.GL_TEXTURE_2D,
        GL10.GL_TEXTURE_MIN_FILTER,
        GL10.GL_LINEAR.toFloat()
      )
      GLES20.glTexParameterf(
        GLES20.GL_TEXTURE_2D,
        GL10.GL_TEXTURE_MAG_FILTER,
        GL10.GL_LINEAR.toFloat()
      )
      GLES20.glTexParameterf(
        GL10.GL_TEXTURE_2D,
        GL10.GL_TEXTURE_WRAP_S,
        GL10.GL_CLAMP_TO_EDGE.toFloat()
      )
      GLES20.glTexParameterf(
        GL10.GL_TEXTURE_2D,
        GL10.GL_TEXTURE_WRAP_T,
        GL10.GL_CLAMP_TO_EDGE.toFloat()
      )

      val texBuffer =
        ByteBuffer.allocateDirect(listBitmap[i].width * listBitmap[i].height * 4).order(ByteOrder.nativeOrder())
      GLES20.glTexImage2D(
        GLES20.GL_TEXTURE_2D, /*level*/
        0,
        GLES30.GL_RGBA8,
        listBitmap[i].width,
        listBitmap[i].height, /*border*/
        0,
        GLES20.GL_RGBA,
        GLES20.GL_UNSIGNED_BYTE,
        texBuffer
      )
      checkGlError("glTexParameter")
    }
    return textures
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

    // Set the textures.
    for (i in texIdArray.indices) {
      GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i)
      GLES20.glBindTexture(
        GLES20.GL_TEXTURE_2D,
        texIdArray[i]
      )
    }

    GLES20.glUniform3fv(
      uAccelerometerCoordinates,
      1,
      accelerometer.getVector().coordinates,
      0
    )
    checkGlError("glUniform3fv")

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


}