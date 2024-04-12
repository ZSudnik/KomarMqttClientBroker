package pl.gov.coi.common.storage.file

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

interface FileConverter {

  fun toUri(file: File): Uri

  fun toFile(
    emptyFile: File,
    uri: Uri,
  )
}

internal class FileConverterImpl(
  private val applicationContext: Context
) : FileConverter {

  override fun toUri(file: File): Uri {
    return FileProvider.getUriForFile(
      applicationContext,
      applicationContext.packageName.toString() + FileFactoryImpl.PROVIDER_REFERENCE,
      file
    )
  }

  override fun toFile(
    emptyFile: File,
    uri: Uri
  ) {
    applicationContext.contentResolver.openInputStream(uri)?.use { inputStream ->
      emptyFile.outputStream().use { fileOutputStream ->
        inputStream.copyTo(fileOutputStream)
      }
    }
  }

}
