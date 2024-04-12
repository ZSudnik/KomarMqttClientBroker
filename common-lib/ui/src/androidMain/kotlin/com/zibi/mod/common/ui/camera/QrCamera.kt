package com.zibi.mod.common.ui.camera

import android.annotation.SuppressLint
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@SuppressLint("RestrictedApi")
@Composable
fun Camera(
  modifier: Modifier = Modifier,
  onQrCodeScanned: (String) -> Unit
) {
  val localLifecycleOwner = LocalLifecycleOwner.current

  AndroidView(
    modifier = modifier,
    factory = { context ->
      val cameraExecutor = Executors.newSingleThreadExecutor()
      val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
      val previewView = PreviewView(context).also {
        it.scaleType = PreviewView.ScaleType.FILL_CENTER
      }
      cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val qrAnalyser = QrAnalyser { rawData ->
          onQrCodeScanned(rawData)
        }

        val imageAnalyser = ImageAnalysis.Builder()
          .setDefaultResolution(Size(
            previewView.width,
            previewView.height
          ))
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build()
          .also {
            it.setAnalyzer(cameraExecutor, qrAnalyser)
          }

        val cameraSelector = CameraSelector.Builder()
          .requireLensFacing(CameraSelector.LENS_FACING_BACK)
          .build()

        try {
          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(
            localLifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyser
          )
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }, ContextCompat.getMainExecutor(context))
      previewView
    }
  )
}