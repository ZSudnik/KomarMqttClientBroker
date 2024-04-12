package com.zibi.mod.common.ui.camera

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrAnalyser(
  val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

  private val supportedImageFormats = listOf(
    ImageFormat.YUV_420_888,
    ImageFormat.YUV_422_888,
    ImageFormat.YUV_444_888,
  )

  private val supportedBarcodeFormats = listOf(
    BarcodeFormat.QR_CODE,
  )

  override fun analyze(imageProxy: ImageProxy) {
    imageProxy.use {
      if (imageProxy.format in supportedImageFormats) {
        val bytes = imageProxy.planes.first().buffer.toByteArray()
        val source = PlanarYUVLuminanceSource(
          bytes,
          imageProxy.width,
          imageProxy.height,
          0,
          0,
          imageProxy.width,
          imageProxy.height,
          false
        )

        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        decodeBinaryBitmap(binaryBitmap)?.run {
          onQrCodeScanned(text)
        }
      }
    }
  }

  private fun decodeBinaryBitmap(binaryBitmap: BinaryBitmap) = try {
    MultiFormatReader().apply {
      setHints(
        mapOf(
          DecodeHintType.POSSIBLE_FORMATS to supportedBarcodeFormats
        )
      )
    }.decode(binaryBitmap)
  } catch (e: Exception) {
    null
  }

  private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
  }
}