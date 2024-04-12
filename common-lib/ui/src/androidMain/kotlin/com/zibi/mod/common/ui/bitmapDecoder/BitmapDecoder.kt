package com.zibi.mod.common.ui.bitmapDecoder

import android.graphics.Bitmap

interface BitmapDecoder {
  fun decodeByteArray(data: ByteArray): Bitmap
  fun decodeResource(data: Int): Bitmap
}