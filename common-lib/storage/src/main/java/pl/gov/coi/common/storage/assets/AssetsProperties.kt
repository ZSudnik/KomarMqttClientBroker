package pl.gov.coi.common.storage.assets

import android.content.Context
import java.util.*

interface AssetsProperties {
  fun load(fileName: String)
  fun getProperty(
    key: String,
    defaultValue: String = "",
  ): String
}

internal class AssetsPropertiesImpl(
  private val applicationContext: Context
) : AssetsProperties {

  private val properties = Properties()

  override fun load(fileName: String) = applicationContext.assets
    .open(fileName)
    .use { inputStream ->
      properties.load(inputStream)
    }

  override fun getProperty(
    key: String,
    defaultValue: String,
  ): String = properties.getProperty(key, defaultValue)
}
