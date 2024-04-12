package com.zibi.mod.data_store.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserLogin constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private var preferences: Preferences? = null

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

    init {
        dataStore.data
            .map { prefs ->
                preferences = prefs
            }
    }

    companion object {
        private val APP_USERNAME = stringPreferencesKey("app_username")
        private val APP_PASSWORD = stringPreferencesKey("app_password")
        private const val APP_USERNAME_DEFAULT = ""
        private const val APP_PASSWORD_DEFAULT = ""
    }
}

