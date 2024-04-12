package com.zibi.mod.common.ui.opengl.shader

data class Shader(
  val title: String,
  val fragmentShader: String,
  val vertexShader: String,
) {
  companion object {
    fun getDefault() =
      Shader(
        fragmentShader = "",
        vertexShader = "",
        title = "Test Title",
      )
  }
}
