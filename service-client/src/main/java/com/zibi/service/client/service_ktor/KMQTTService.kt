package com.zibi.service.client.service_ktor

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.zibi.mod.data_store.data.Topic
import com.zibi.mod.data_store.preferences.ClientSetting
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.service.client.notification.NotificationUtil
import com.zibi.service.client.service.ClientProperties
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.cio.CIO
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.zibi.komar.mclient.ktor.Mqtt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class KxMQTTService : Service() {

    private val clientSetting: ClientSetting by inject()
    private val lightBulbStore: LightBulbStore by inject()

    private var serviceJob: Job? = null
    private lateinit var scope: CoroutineScope
    private var isConn: Boolean? = null

    private val env = applicationEngineEnvironment {
        module {
            install(Mqtt) {
                initSubscriptions(topics = Topic.list)
            }
//            configureMqtt()
        }
        connector {
            host = "192.168.73.25"
            port = 1883
        }
    }
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

    init {
        scope = CoroutineScope( Dispatchers.IO )
//        CoroutineScope(Dispatchers.IO).launch {
//            embeddedServer(CIO, port = 8080, host = "localhost") {
//                configureMqtt()
//            }.start(wait = false)
//        }
    }

    override fun onCreate() {
        super.onCreate()
//        startForeground(NOT_SERVICE_ID, NotificationUtil(this).notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (this::scope.isInitialized && serviceJob == null)
            serviceJob = scope.launch {
                embeddedServer(CIO, env).start(true)
                val client = env.application.plugin(Mqtt)
//                Mqtt.client.reloadConfiguration(prop)
                client.addOnMessageArrived( lightBulbStore::setMessageArrived)
                client.addOnOffMonitorConnection{ value -> onOffClient(value) }
//                client.connectAuto()
//                isClientRunning.value = KxMQTTWrapper.startClientAuto(
//                    prop = getClientProperties(),
//                    lightBulbStore = lightBulbStore,
//                    coroutineScope = this,
//                    errorConnect = { value -> onOffClient(value) }
//                )
            }
        return START_STICKY
    }

    private fun onOffClient(value: Boolean){
        if(isConn != value) {
            isClientRunning.value = value
//            startForeground(NOT_SERVICE_ID, NotificationUtil(this@KxMQTTService).notification)
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
        KxMQTTWrapper.stopClient()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    companion object {
        const val NOT_SERVICE_ID = 987
        var isClientRunning = mutableStateOf(false)

        fun onChangeConnection(context: Context){
            val intent = Intent(context, KxMQTTService::class.java)
            if (isClientRunning.value) {
                isClientRunning.value = false
                KxMQTTWrapper.disConnectClient()
//                context.stopService(intent)
            }else
                ContextCompat.startForegroundService(context, intent)
        }

        fun end(context: Context) {
            val intent = Intent(context, KxMQTTService::class.java)
            context.stopService(intent)
        }

        fun publish(topic: String, message: String) =
            KxMQTTWrapper.onPublishCommand(topic = topic, message = message,)

    }
}