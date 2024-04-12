package pl.gov.coi.common.storage.file

import android.content.Context
import java.io.*

interface FileManager {
  fun saveFileFromStream(input: InputStream, filePath: String): Boolean

  fun deleteFile(filePath: String): Boolean

  fun getFileList(filePath: String): List<File>

  fun isFileExisting(filePath: String): Boolean

  fun getDataFromAssetFile(fileName: String): String

}

internal class FileManagerImpl(
  private val applicationContext: Context
) : FileManager {

  override fun saveFileFromStream(input: InputStream, filePath: String): Boolean {
    try {
      val fos = FileOutputStream(filePath)
      fos.use { output ->
        val buffer = ByteArray(BUFFER_SIZE)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
          output.write(buffer, 0, read)
        }
        output.flush()
      }
      return true
    } catch (e: Exception) {
      return false
    } finally {
      input.close()
    }
  }

  override fun deleteFile(filePath: String): Boolean =
    File(filePath).run { if (isDirectory) false else delete() }


  override fun getFileList(filePath: String): List<File> = File(filePath).run {
    if (!isDirectory) return emptyList()
    listFiles()?.asList() ?: emptyList()
  }

  override fun isFileExisting(filePath: String): Boolean = File(filePath).exists()

  override fun getDataFromAssetFile(fileName: String): String {
    var fileContent = ""
    var inputStreamReader: InputStreamReader? = null
    try {
      inputStreamReader = InputStreamReader(applicationContext.assets?.open(fileName))
      fileContent = inputStreamReader.readText()
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      inputStreamReader?.close()
    }
    return fileContent
  }

  companion object {
    private const val BUFFER_SIZE = 4 * 1024
  }
}

