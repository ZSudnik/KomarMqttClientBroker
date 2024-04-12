package com.zibi.mod.common.ui.opengl.renderer

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import com.zibi.mod.common.ui.opengl.shader.Shader


interface IRenderer : GLSurfaceView.Renderer {

  val context: Context
  val shader: Shader
  val listBitmap: List<Bitmap>

  fun onResume() {}
  fun onPause() {}
  fun startAccelerometer(isPreview: Boolean) {}

}



