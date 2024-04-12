package com.zibi.mod.common.ui.bitmapDecoder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class BitmapDecoderImpl constructor(private val context: Context) : BitmapDecoder {
  override fun decodeByteArray(data: ByteArray): Bitmap = BitmapFactory
    .decodeByteArray(data, 0, data.size)
  override fun decodeResource(data: Int): Bitmap = BitmapFactory.decodeResource(
    context.resources,
    data
  )
}