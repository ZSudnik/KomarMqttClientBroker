package com.zibi.mod.common.ui.opengl.util


import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


private const val TAG = "GlUtil"
const val SIZEOF_FLOAT = 4

/**
 * Identity matrix for general use.  Don't modify or life will get weird.
 */
val IDENTITY_MATRIX: FloatArray = FloatArray(16)
  get() {
    Matrix.setIdentityM(
      field,
      0
    )
    return field
  }

/**
 * Creates a new program from the supplied vertex and fragment shaders.
 * @return A handle to the program, or 0 on failure.
 */
fun createProgram(
  vertexSource: String?,
  fragmentSource: String?
): Int {
  val vertexShader = loadShader(
    GLES20.GL_VERTEX_SHADER,
    vertexSource
  )
  if (vertexShader == 0) {
    return 0
  }
  val pixelShader = loadShader(
    GLES20.GL_FRAGMENT_SHADER,
    fragmentSource
  )
  if (pixelShader == 0) {
    return 0
  }
  var program = GLES20.glCreateProgram()
  checkGlError("glCreateProgram")
  if (program == 0) {
    Log.e(
      TAG,
      "Could not create program"
    )
  }
  GLES20.glAttachShader(
    program,
    vertexShader
  )
  checkGlError("glAttachShader")
  GLES20.glAttachShader(
    program,
    pixelShader
  )
  checkGlError("glAttachShader")
  GLES20.glLinkProgram(program)
  val linkStatus = IntArray(1)
  GLES20.glGetProgramiv(
    program,
    GLES20.GL_LINK_STATUS,
    linkStatus,
    0
  )
  if (linkStatus[0] != GLES20.GL_TRUE) {
    Log.e(
      TAG,
      "Could not link program: "
    )
    Log.e(
      TAG,
      GLES20.glGetProgramInfoLog(program)
    )
    GLES20.glDeleteProgram(program)
    program = 0
  }
  return program
}

/**
 * Compiles the provided shader source.
 * @return A handle to the shader, or 0 on failure.
 */
fun loadShader(
  shaderType: Int,
  source: String?
): Int {
  var shader = GLES20.glCreateShader(shaderType)
  checkGlError("glCreateShader type=$shaderType")
  GLES20.glShaderSource(
    shader,
    source
  )
  GLES20.glCompileShader(shader)
  val compiled = IntArray(1)
  GLES20.glGetShaderiv(
    shader,
    GLES20.GL_COMPILE_STATUS,
    compiled,
    0
  )
  if (compiled[0] == 0) {
    Log.e(
      TAG,
      "Could not compile shader $shaderType:"
    )
    Log.e(
      TAG,
      " " + GLES20.glGetShaderInfoLog(shader)
    )
    GLES20.glDeleteShader(shader)
    shader = 0
  }
  return shader
}

/**
 * Checks to see if a GLES error has been raised.
 */
fun checkGlError(op: String) {
  val error = GLES20.glGetError()
  if (error != GLES20.GL_NO_ERROR) {
    val msg = op + ": glError 0x" + Integer.toHexString(error)
    throw RuntimeException(msg)
  }
}

/**
 * Checks to see if the location we obtained is valid.  GLES returns -1 if a label
 * could not be found, but does not set the GL error.
 * Throws a RuntimeException if the location is invalid.
 */
fun checkLocation(
  location: Int,
  label: String
) {
  if (location < 0) {
    throw RuntimeException("Unable to locate '$label' in program")
  }
}

/**
 * Creates a texture from raw data.
 *
 * @param data   Image data, in a "direct" ByteBuffer.
 * @param width  Texture width, in pixels (not bytes).
 * @param height Texture height, in pixels.
 * @return Handle to texture.
 */
fun createImageTexture(
  width: Int,
  height: Int
): Int {
  val texBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder())

  val textureHandles = IntArray(1)
  GLES20.glGenTextures(
    1,
    textureHandles,
    0
  )
  val textureHandle: Int = textureHandles[0]
  checkGlError("glGenTextures")

  // Bind the texture handle to the 2D texture target.
  GLES20.glBindTexture(
    GLES20.GL_TEXTURE_2D,
    textureHandle
  )

  // Configure min/mag filtering, i.e. what scaling method do we use if what we're rendering
  // is smaller or larger than the source image.
  GLES20.glTexParameteri(
    GLES20.GL_TEXTURE_2D,
    GLES20.GL_TEXTURE_MIN_FILTER,
    GLES20.GL_LINEAR
  )
  GLES20.glTexParameteri(
    GLES20.GL_TEXTURE_2D,
    GLES20.GL_TEXTURE_MAG_FILTER,
    GLES20.GL_LINEAR
  )
  checkGlError("loadImageTexture")

  // Load the data from the buffer into the texture handle.
  GLES20.glTexImage2D(
    GLES20.GL_TEXTURE_2D, /*level*/
    0,
    GLES20.GL_RGBA,
    width,
    height, /*border*/
    0,
    GLES20.GL_RGBA,
    GLES20.GL_UNSIGNED_BYTE,
    texBuffer
  )
  checkGlError("loadImageTexture")
  return textureHandle
}

/**
 * Allocates a direct float buffer, and populates it with the float array data.
 */
fun createFloatBuffer(coords: FloatArray): FloatBuffer {
  val bb = ByteBuffer.allocateDirect(coords.size * SIZEOF_FLOAT)
  bb.order(ByteOrder.nativeOrder())
  val fb = bb.asFloatBuffer()
  fb.put(coords)
  fb.position(0)
  return fb
}

fun GL10.use(
  feature: Int,
  block: GL10.() -> Unit
) {
  glEnable(feature)
  checkGlError("glEnable $feature")
  block()
  glDisable(feature)
  checkGlError("glDisable $feature")
}