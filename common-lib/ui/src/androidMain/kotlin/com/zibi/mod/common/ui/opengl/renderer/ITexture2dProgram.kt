package com.zibi.mod.common.ui.opengl.renderer

import java.nio.FloatBuffer

interface ITexture2dProgram {

  fun draw(
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
  )


}