package pl.gov.coi.common.storage.file

import android.os.Environment
import java.io.File

interface EnvFileProvider {
  fun getDownloadDir(): File
}

internal class EnvFileProviderImpl : EnvFileProvider {

  override fun getDownloadDir(): File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
}