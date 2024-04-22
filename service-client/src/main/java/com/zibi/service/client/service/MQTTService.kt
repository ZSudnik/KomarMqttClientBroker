package com.zibi.service.client.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.zibi.mod.data_store.preferences.ClientSetting
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.service.client.notification.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MQTTService : Service() {

    private val clientSetting: ClientSetting by inject()
    private val lightBulbStore: LightBulbStore by inject()

    private var serviceJob: Job? = null
    private lateinit var scope: CoroutineScope
    private var isConn: Boolean? = null

    private fun cancelJob(){
        serviceJob?.cancel()
        serviceJob = null
    }

    private suspend fun getClientProperties(): ClientProperties {
        return ClientProperties(
            host = clientSetting.mqttBrokerHostFirst(),
            port = clientSetting.mqttBrokerPortFirst(),
            clientIdentifier = clientSetting.mqttMyIdentifierFirst(),
            userName = clientSetting.mqttMyNameFirst(),
            password = clientSetting.mqttBrokerPasswordFirst().toByteArray(),
        )
    }

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope( Dispatchers.IO )
        startForeground(NOT_SERVICE_ID, NotificationUtil(this).notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (this::scope.isInitialized && serviceJob == null)
            serviceJob = scope.launch {
                isClientRunning.value = MQTTWrapper.startClientAuto(
                    prop = getClientProperties(),
                    lightBulbStore = lightBulbStore,
                    coroutineScope = this,
                    errorConnect = { value -> errorClient(value) }
                )
            }
        return START_STICKY
    }

    private fun errorClient(value: Boolean){
        if(isConn != value) {
            isClientRunning.value = value
            startForeground(NOT_SERVICE_ID, NotificationUtil(this@MQTTService).notification)
            isConn = value
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
        isClientRunning.value = false
        MQTTWrapper.stopClient()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    companion object {
        const val NOT_SERVICE_ID = 987
        var isClientRunning = mutableStateOf(false)

        fun onChangeConnection(context: Context){
            val intent = Intent(context, MQTTService::class.java)
            if (isClientRunning.value) {
                isClientRunning.value = false
                MQTTWrapper.disConnectClient()
//                context.stopService(intent)
            }else
                ContextCompat.startForegroundService(context, intent)
        }

        fun end(context: Context) {
            val intent = Intent(context, MQTTService::class.java)
            context.stopService(intent)
        }

        fun publish(topic: String, message: String) =
            MQTTWrapper.onPublishCommand(topic = topic, message = message,)

    }
}