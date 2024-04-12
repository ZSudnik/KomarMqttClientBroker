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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BrokerSetting(
    context: Context,
//    private val dataStore: DataStore<Preferences>,
) {


    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES)),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
    )

    private val mqttPort: Flow<Int>
        get() = dataStore.data.map { it[MQTT_PORT] ?: MQTT_PORT_DEFAULT }
    suspend fun mqttPortFirst(): Int {
        return mqttPort.first()
    }
    suspend fun setMqttPort(value: Int) {
        dataStore.edit { it[MQTT_PORT] = value }
    }

    private val wsEnabled: Flow<Boolean>
        get() = dataStore.data.map { it[WS_ENABLED] ?: WS_ENABLED_DEFAULT }
    suspend fun wsEnabledFirst(): Boolean {
        return wsEnabled.first()
    }
    suspend fun setWSEnabled(value: Boolean) {
        dataStore.edit { it[WS_ENABLED] = value }
    }

    private val wsPort: Flow<Int>
        get() = dataStore.data.map { it[WS_PORT] ?: WS_PORT_DEFAULT }
    suspend fun wsPortFirst(): Int {
        return wsPort.first()
    }
    suspend fun setWSPort(value: Int) {
        dataStore.edit { it[WS_PORT] = value }
    }

    private val wsPath: Flow<String>
        get() = dataStore.data.map { it[WS_PATH] ?: WS_PATH_DEFAULT }
    suspend fun wsPathFirst(): String {
        return wsPath.first()
    }
    suspend fun setWSPath(value: String) {
        dataStore.edit { it[WS_PATH] = value }
    }

    private val authEnabled: Flow<Boolean>
        get() = dataStore.data.map { it[AUTH_ENABLED] ?: AUTH_ENABLED_DEFAULT }
    suspend fun authEnabledFirst(): Boolean {
        return authEnabled.first()
    }
    suspend fun setAuthEnabled(value: Boolean) {
        dataStore.edit { it[AUTH_ENABLED] = value }
    }

    private val userName: Flow<String>
        get() = dataStore.data.map { it[UNAME] ?: UNAME_DEFAULT }
    suspend fun userNameFirst(): String {
        return userName.first()
    }
    suspend fun setUsername(value: String) {
        dataStore.edit { it[UNAME] = value }
    }

    private val password: Flow<String>
        get() = dataStore.data.map { it[PWD] ?: PWD_DEFAULT }
    suspend fun passwordFirst(): String {
        return password.first()
    }
    suspend fun setPassword(value: String) {
        dataStore.edit { it[PWD] = value }
    }

    private val allowTasmota: Flow<Boolean>
        get() = dataStore.data.map { it[TASMOTA] ?: TASMOTA_DEFAULT }
    suspend fun allowTasmotaFirst(): Boolean {
        return allowTasmota.first()
    }
    suspend fun setAllowTasmota(value: Boolean) {
        dataStore.edit { it[TASMOTA] = value }
    }


    suspend fun getServerProperties(): BrokerProperties {
        return BrokerProperties(
                mqttPort = mqttPortFirst(),
                wsEnabled = wsEnabledFirst(),
                wsPort = wsPortFirst(),
                wsPath = wsPathFirst(),
                authEnabled = authEnabledFirst(),
                userName = userNameFirst(),
                password = passwordFirst(),
                allowTasmota = allowTasmotaFirst(),
            )
        }

    companion object {
        private val MQTT_PORT = intPreferencesKey("mqtt_port")
        private val WS_ENABLED = booleanPreferencesKey("ws_enabled")
        private val WS_PORT = intPreferencesKey("ws_port")
        private val WS_PATH = stringPreferencesKey("ws_path")
        private val AUTH_ENABLED = booleanPreferencesKey("auth_enabled")
        private val UNAME = stringPreferencesKey("username")
        private val PWD = stringPreferencesKey("password")
        private val TASMOTA = booleanPreferencesKey("tasmota")

        const val MQTT_PORT_DEFAULT = 1883
        const val WS_ENABLED_DEFAULT = true
        const val WS_PORT_DEFAULT = 8883
        const val WS_PATH_DEFAULT = "/mqtt"
        const val AUTH_ENABLED_DEFAULT = false
        const val UNAME_DEFAULT = ""
        const val PWD_DEFAULT = ""
        const val TASMOTA_DEFAULT = true

        const val USER_PREFERENCES = "sdfgh"
    }
}

