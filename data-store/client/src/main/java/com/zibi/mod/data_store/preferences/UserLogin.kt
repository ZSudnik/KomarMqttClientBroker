package com.zibi.mod.data_store.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserLogin(
    context: Context,
) {

    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(SharedPreferencesMigration(context, LOGIN_PREFERENCES)),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(LOGIN_PREFERENCES) }
    )

    suspend fun userName(): String =
        dataStore.data.map { it[APP_USERNAME] ?: APP_USERNAME_DEFAULT }.first()

    suspend fun password(): String =
        dataStore.data.map { it[APP_PASSWORD] ?: APP_PASSWORD_DEFAULT }.first()


    suspend fun setUsername(value: String) {
        dataStore.edit { it[APP_USERNAME] = value }
    }

    suspend fun setPassword(value: String) {
        dataStore.edit { it[APP_PASSWORD] = value }
    }

//    init {
//        dataStore.data
//            .map { prefs ->
//                preferences = prefs
//            }
//    }

    companion object {
        private val APP_USERNAME = stringPreferencesKey("app_username")
        private val APP_PASSWORD = stringPreferencesKey("app_password")
        private const val APP_USERNAME_DEFAULT = ""
        private const val APP_PASSWORD_DEFAULT = ""
        private const val LOGIN_PREFERENCES = "login_preferences"
    }
}

