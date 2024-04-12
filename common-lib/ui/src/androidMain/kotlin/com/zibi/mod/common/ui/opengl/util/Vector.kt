package com.zibi.mod.common.ui.opengl.util

class Vector(
  private val x: Float,
  private val y: Float,
  private val z: Float
) {

  val coordinates: FloatArray
    get() {
      return floatArrayOf(
        x,
        y,
        z
      )
    }

  fun equalXYZ(newVector: Vector): Boolean {
    return this.x == newVector.x && this.y == newVector.y && this.z == newVector.z
  }

  fun equalXY(newVector: Vector): Boolean {
    return this.x == newVector.x && this.y == newVector.y && this.z == newVector.z
  }

  fun equalZERO(): Boolean {
    return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f
  }

  override fun toString(): String {
    return "Vector( ${"%.4f".format(x)}f, ${"%.4f".format(y)}f, ${"%.4f".format(z)}f ),"
  }

  companion object {
    val ZERO_VECTOR = Vector(
      0.0f,
      0.0f,
      0.0f
    )
    val MAX_VECTOR = Vector(
      Float.MAX_VALUE,
      Float.MAX_VALUE,
      Float.MAX_VALUE
    )

    fun sum(vararg vectors: Vector): Vector {
      var x = 0.0f
      var y = 0.0f
      var z = 0.0f
      for (vector in vectors) {
        x += vector.x
        y += vector.y
        z += vector.z
      }
      return Vector(
        x,
        y,
        z
      )
    }

    var max = 0.0

  }
}