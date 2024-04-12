package com.zibi.mod.common.ui.opengl.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import com.zibi.mod.common.ui.opengl.renderer.IRenderer


@SuppressLint("ViewConstructor")
class DrawGLSurfaceView constructor(
  context: Context,
  renderer: IRenderer,
  isTransparent: Boolean
) : GLSurfaceView(context) {

  init {
    // Create an OpenGL ES 3.0 context
    setEGLContextClientVersion(3)
    if (isTransparent) setZOrderOnTop(true)
    setEGLConfigChooser(
      8,
      8,
      8,
      8,
      16,
      0
    )
    holder.setFormat(PixelFormat.RGBA_8888)
//        preserveEGLContextOnPause = true
    setRenderer(renderer)

  }

}