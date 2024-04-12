package com.zibi.mod.common.ui.opengl.renderer.wave

import android.opengl.GLES20
import com.zibi.mod.common.ui.opengl.renderer.ITexture2dProgram
import com.zibi.mod.common.ui.opengl.shader.Shader
import com.zibi.mod.common.ui.opengl.util.checkGlError
import com.zibi.mod.common.ui.opengl.util.checkLocation
import com.zibi.mod.common.ui.opengl.util.createProgram
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * GL program and supporting functions for textured 2D shapes.
 */
class Texture2dProgram(val shader: Shader) : ITexture2dProgram {

  // Handles to the GL program and various components of it.
  private var mProgramHandle: Int
  private val aPositionLoc: Int
  private val uTime: Int
  private val muMVPMatrixLoc: Int

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

    aPositionLoc = GLES20.glGetAttribLocation(
      mProgramHandle,
      "a_Position"
    )
    checkLocation(
      aPositionLoc,
      "a_Position"
    )
    uTime = GLES20.glGetUniformLocation(
      mProgramHandle,
      "u_Time"
    )
    checkLocation(
      uTime,
      "u_Time"
    )

    muMVPMatrixLoc = GLES20.glGetUniformLocation(
      mProgramHandle,
      "u_MVPMatrix"
    )
    checkLocation(
      muMVPMatrixLoc,
      "u_MVPMatrix"
    )
  }

  fun createTexture(
    width: Int,
    height: Int
  ): Int {
    val frameBuffer = IntBuffer.allocate(1)
    GLES20.glGenFramebuffers(
      1,
      frameBuffer
    )
    GLES20.glBindFramebuffer(
      GLES20.GL_FRAMEBUFFER,
      frameBuffer.get(0)
    )

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

    GLES20.glTexImage2D(
      GLES20.GL_TEXTURE_2D,
      0,
      GLES20.GL_RGBA,
      width,
      height,
      0,
      GLES20.GL_RGBA,
      GLES20.GL_UNSIGNED_BYTE,
      null
    )
    GLES20.glFramebufferTexture2D(
      GLES20.GL_FRAMEBUFFER,
      GLES20.GL_COLOR_ATTACHMENT0,
      GLES20.GL_TEXTURE_2D,
      texId,
      0
    )

    GLES20.glBindFramebuffer(
      GLES20.GL_FRAMEBUFFER,
      0
    )
    checkGlError("glTexParameter")
    return texId
  }

  /**
   * Issues the draw call.  Does the full setup on every call.
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
    } // Copy the model / view / projection matrix over.
    GLES20.glUniformMatrix4fv(
      muMVPMatrixLoc,
      1,
      false,
      mvpMatrix,
      0
    )
    checkGlError("glUniformMatrix4fv")

    // Enable the "aPositionLoc" vertex attribute.
    GLES20.glEnableVertexAttribArray(aPositionLoc)
    checkGlError("glEnableVertexAttribArray")

    // Connect vertexBuffer to "a_VertexPosition".
    GLES20.glVertexAttribPointer(
      aPositionLoc,
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
    } // Draw the rect.
    GLES20.glDrawArrays(
      GLES20.GL_TRIANGLE_STRIP,
      firstVertex,
      vertexCount
    )
    checkGlError("glDrawArrays")

    // Done -- disable vertex array, texture, and program.
    GLES20.glDisableVertexAttribArray(aPositionLoc)
    GLES20.glDisableVertexAttribArray(uTime)
    GLES20.glBindTexture(
      GLES20.GL_TEXTURE_2D,
      0
    )
    GLES20.glUseProgram(0)
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

}