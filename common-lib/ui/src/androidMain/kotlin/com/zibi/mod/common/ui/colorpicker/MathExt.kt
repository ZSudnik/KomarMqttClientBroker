package com.zibi.mod.common.ui.colorpicker

import kotlin.math.PI

fun Float.toRadian(): Float = this / 180.0f * PI.toFloat()
internal fun Double.toRadian(): Double = this / 180 * PI
fun Float.toDegree(): Float = this * 180.0f / PI.toFloat()
fun Double.toDegree(): Double = this * 180 / PI
