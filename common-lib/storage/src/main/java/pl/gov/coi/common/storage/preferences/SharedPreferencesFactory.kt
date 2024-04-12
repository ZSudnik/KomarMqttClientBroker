package pl.gov.coi.common.storage.preferences

import android.content.Context

class SharedPreferencesFactory constructor(
  private val context: Context,
) {

  fun createPlainSharedPreferences(fileName: String) =
    context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
      ?: error("Failed to create shared preferences with name: $fileName")

}
