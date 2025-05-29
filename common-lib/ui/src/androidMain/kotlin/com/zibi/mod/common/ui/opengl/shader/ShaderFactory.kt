package com.zibi.mod.common.ui.opengl.shader

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.opengl.TypeShader
import com.zibi.mod.common.ui.opengl.renderer.IRenderer
import com.zibi.mod.common.ui.opengl.renderer.basic.BasicRenderer
import com.zibi.mod.common.ui.opengl.renderer.hologram.HoloRenderer
import com.zibi.mod.common.ui.opengl.renderer.wave.WaveRenderer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ShaderFactory {


  fun getRenderer(
    context: Context,
    typHolo: TypeShader,
    resListBitmap: List<Int>
  ): IRenderer {
    return when (typHolo) {
      TypeShader.Basic -> BasicRenderer(
        context = context,
        shader = Shader(
          title = "No effect",
          fragmentShader = context.readTextFileFromResource(R.raw.common_ui_basic_fragment),
          vertexShader = context.readTextFileFromResource(R.raw.common_ui_basic_vertex)
        ),
        listBitmap = context.getListBitmaps(resListBitmap)
      )
      TypeShader.WavingBitmap -> WaveRenderer(
        context = context,
        shader = Shader(
          title = "",
          fragmentShader = context.readTextFileFromResource(R.raw.common_ui_waving_bitmap_fragment),
          vertexShader = context.readTextFileFromResource(R.raw.common_ui_waving_bitmap_vertex)
        ),
        listBitmap = context.getListBitmaps(resListBitmap)
      )
      TypeShader.Hologram -> HoloRenderer(
        context = context,
        shader = Shader(
          title = "Hologram 1",
          fragmentShader = context.readTextFileFromResource(R.raw.common_ui_holo__fragment),
          vertexShader = context.readTextFileFromResource(R.raw.common_ui_holo__vertex)
        ),
        listBitmap = context.getListBitmaps(resListBitmap)
      )
      TypeShader.HologramII -> HoloRenderer(
        context = context,
        shader = Shader(
          title = "Hologram 2",
          fragmentShader = context.readTextFileFromResource(R.raw.common_ui_holo_ii_fragment),
          vertexShader = context.readTextFileFromResource(R.raw.common_ui_holo_ii_vertex)
        ),
        listBitmap = context.getListBitmaps(resListBitmap)
      )
    }
  }


  private fun Context.readTextFileFromResource(resourceId: Int): String {
    val body = StringBuilder()
    try {
      val inputStream = resources.openRawResource(resourceId)
      val inputStreamReader = InputStreamReader(inputStream)
      val bufferedReader = BufferedReader(inputStreamReader)
      var nextLine: String?
      while (bufferedReader.readLine().also { nextLine = it } != null) {
        body.append(nextLine)
        body.append('\n')
      }
    } catch (e: IOException) {
      throw RuntimeException(
        "Could not open resource: $resourceId",
        e
      )
    } catch (nfe: Resources.NotFoundException) {
      throw RuntimeException(
        "Resource not found: $resourceId",
        nfe
      )
    }
    return body.toString()
  }

  private fun Context.getListBitmaps(listResBitmaps: List<Int>): List<Bitmap> {
    val listBitmap: MutableList<Bitmap> = mutableListOf()
    var resBitmap: Int = -1
    try {
      for (element in listResBitmaps) {
        resBitmap = element
        listBitmap.add(
          AppCompatResources.getDrawable(
            this,
            resBitmap
          )!!.toBitmap()
        )
      }
    } catch (e: IOException) {
      throw RuntimeException(
        "Could not open resource: $resBitmap",
        e
      )
    } catch (nfe: Resources.NotFoundException) {
      throw RuntimeException(
        "Resource not found: $resBitmap",
        nfe
      )
    }
    return listBitmap
  }

}