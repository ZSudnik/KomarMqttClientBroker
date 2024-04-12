package com.zibi.mod.data_store.preferences

import android.content.Context
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

class ClientSetting(
    context: Context,
) {
    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { emptyPreferences() }
        ),
        migrations = listOf(SharedPreferencesMigration(context, USER_PREFERENCES)),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
    )

    private val firstShotClientMqtt = ClientProperties()

    private val mqttBrokerPort: Flow<Int>
        get() {
            return dataStore.data.map { it[MQTT_BROKER_PORT] ?: firstShotClientMqtt.port }
        }
    suspend fun mqttBrokerPortFirst(): Int {
        return mqttBrokerPort.first()
    }
    suspend fun setMqttBrokerPort(value: Int) {
        dataStore.edit { it[MQTT_BROKER_PORT] = value }
    }

    private val mqttBrokerHost: Flow<String>
        get() {
            return dataStore.data.map { it[MQTT_BROKER_HOST] ?: firstShotClientMqtt.host }
        }
    suspend fun mqttBrokerHostFirst(): String {
        return mqttBrokerHost.first()
    }
    suspend fun setMqttBrokerHost(value: String) {
        dataStore.edit { it[MQTT_BROKER_HOST] = value }
    }

    private val mqttMyIdentifier: Flow<String>
        get() {
            return dataStore.data.map { it[MQTT_MY_IDENTIFIER] ?: firstShotClientMqtt.clientIdentifier }
        }
    suspend fun setMqttMyIdentifier(value: String) {
        dataStore.edit { it[MQTT_MY_IDENTIFIER] = value }
    }
    suspend fun mqttMyIdentifierFirst(): String {
        return mqttMyIdentifier.first()
    }

    private val mqttMyName: Flow<String>
        get() {
            return dataStore.data.map { it[MQTT_MY_USER_NAME] ?: firstShotClientMqtt.userName }
        }
    suspend fun mqttMyNameFirst(): String {
            return mqttMyName.first()
        }
    suspend fun setMqttMyName(value: String) {
        dataStore.edit { it[MQTT_MY_USER_NAME] = value }
    }

    private val mqttBrokerPassword: Flow<String>
        get() {
            return dataStore.data.map { it[MQTT_BROKER_PASSWORD] ?: MQTT_BROKER_PASSWORD_DEFAULT }
        }
    suspend fun mqttBrokerPasswordFirst(): String {
        return mqttBrokerPassword.first()
    }
    suspend fun setMqttBrokerPassword(value: String) {
        dataStore.edit { it[MQTT_BROKER_PASSWORD] = value }
    }

    private val userName: Flow<String>
        get() {
            return dataStore.data.map { it[MQTT_BROKER_USERNAME] ?: MQTT_BROKER_USERNAME_DEFAULT }
        }
    suspend fun mqttBrokerUserNameFirst(): String {
        return userName.first()
    }
    suspend fun setMqttBrokerUsername(value: String) {
        dataStore.edit { it[MQTT_BROKER_USERNAME] = value }
    }

    suspend fun getClientProperties(): ClientProperties {
        return ClientProperties(
            host = mqttBrokerHostFirst(),
            port =  mqttBrokerPortFirst(),
            clientIdentifier = mqttMyIdentifierFirst(),
            userName = mqttMyNameFirst(),
            password = mqttBrokerPasswordFirst().toByteArray(),
        )
    }

    //////////////////////////////////////////////////
    private val wsEnabled: Flow<Boolean>
        get() {
            return dataStore.data.map { it[WS_ENABLED] ?: WS_ENABLED_DEFAULT }
        }
    suspend fun wsEnabledFirst(): Boolean {
        return wsEnabled.first()
    }
    suspend fun setWSEnabled(value: Boolean) {
        dataStore.edit { it[WS_ENABLED] = value }
    }

    private val wsPort: Flow<Int>
        get() {
            return dataStore.data.map { it[WS_PORT] ?: WS_PORT_DEFAULT }
        }
    suspend fun wsPortFirst(): Int {
        return wsPort.first()
    }
    suspend fun setWSPort(value: Int) {
        dataStore.edit { it[WS_PORT] = value }
    }

    private val wsPath: Flow<String>
        get() {
            return dataStore.data.map { it[WS_PATH] ?: WS_PATH_DEFAULT }
        }
    suspend fun wsPathFirst(): String {
        return wsPath.first()
    }
    suspend fun setWSPath(value: String) {
        dataStore.edit { it[WS_PATH] = value }
    }

    private val authEnabled: Flow<Boolean>
        get() {
            return dataStore.data.map { it[AUTH_ENABLED] ?: AUTH_ENABLED_DEFAULT }
        }
    suspend fun authEnabledFirst(): Boolean {
        return authEnabled.first()
    }
    suspend fun setAuthEnabled(value: Boolean) {
        dataStore.edit { it[AUTH_ENABLED] = value }
    }

    companion object {
        private val MQTT_BROKER_PORT = intPreferencesKey("mqtt_broker_port")
        private val MQTT_BROKER_HOST = stringPreferencesKey("mqtt_broker_host")
        private val MQTT_MY_IDENTIFIER = stringPreferencesKey("mqtt_clientIdentifier")
        private val MQTT_MY_USER_NAME = stringPreferencesKey("mqtt_userName")
        private val MQTT_BROKER_PASSWORD = stringPreferencesKey("mqtt_password")
        private val MQTT_BROKER_USERNAME = stringPreferencesKey("mqtt_username")

        private val WS_ENABLED = booleanPreferencesKey("ws_enabled")
        private val WS_PORT = intPreferencesKey("ws_port")
        private val WS_PATH = stringPreferencesKey("ws_path")
        private val AUTH_ENABLED = booleanPreferencesKey("auth_enabled")

        const val MQTT_PORT_DEFAULT = 1883
        const val MQTT_HOST_DEFAULT = "192.168.73.25"
        const val WS_ENABLED_DEFAULT = false
        const val WS_PORT_DEFAULT = 8080
        const val WS_PATH_DEFAULT = "/mqtt"
        const val AUTH_ENABLED_DEFAULT = false
        const val MQTT_BROKER_USERNAME_DEFAULT = ""
        const val MQTT_BROKER_PASSWORD_DEFAULT = ""
        const val MQTT_CLIENT_USERNAME_DEFAULT = "Zibi"
        const val MQTT_CLIENT_IDENTIFIER_DEFAULT = "zibi"

        private const val USER_PREFERENCES = "user_preferences"
    }
}

