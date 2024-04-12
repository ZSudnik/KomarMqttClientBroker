package pl.gov.coi.common.storage.file

import android.content.Context
import android.os.Environment
import java.io.File

interface FileFactory {

  enum class FileDirectory {
    PICTURES,
  }

  suspend fun create(
    name: String,
    extension: String,
    directory: FileDirectory,
  ): File
}

internal class FileFactoryImpl(
  private val applicationContext: Context
) : FileFactory {

  override suspend fun create(
    name: String,
    extension: String,
    directory: FileFactory.FileDirectory,
  ): File {
    val storageDir = when (directory) {
      FileFactory.FileDirectory.PICTURES ->
        applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }
    return File(
      storageDir,
      name + extension,
    ).apply {
      createNewFile()
    }
  }

  companion object {
    const val PROVIDER_REFERENCE = ".provider"
  }
}
