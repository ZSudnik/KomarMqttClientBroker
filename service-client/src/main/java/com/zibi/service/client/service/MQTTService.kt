package com.zibi.service.client.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.zibi.mod.data_store.preferences.ClientSetting
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.service.client.notification.NotificationUtil
import com.zibi.service.client.util.Observer
import com.zibi.service.client.util.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MQTTService : Service(), Subject {

//    private val clientSetting: ClientSetting by inject()
//    private val lightBulbStore: LightBulbStore by inject()

    // Binder given to clients.
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getMqttService(): MQTTService = this@MQTTService
    }

    private val middleClient = MiddleClient()
    private lateinit var notificationUtil: NotificationUtil
    private var observers: Observer? = null

    override fun notifyObservers(isRunning: Boolean) {
        observers?.let {
            it.update(isRunning)
        }
    }

    private var serviceJob: Job? = null
    private var serviceScope = CoroutineScope(Dispatchers.IO)
    private var isConn: Boolean = false

    private fun cancelJob() {
        serviceJob?.cancel()
        serviceJob = null
    }

    fun publish(topic: String, message: String) {
        middleClient.onPublishCommand(topic = topic, message = message)
    }

    fun addObserver(observer: Observer) {
        observers = observer
    }

    fun onConnected(clientSetting: ClientSetting, lightBulbStore: LightBulbStore) {
        serviceJob = serviceScope.launch {
            this@MQTTService.notifyObservers(
                middleClient.startClientAuto(
                    prop = ClientProperties(
                        host = clientSetting.mqttBrokerHostFirst(),
                        port = clientSetting.mqttBrokerPortFirst(),
                        clientIdentifier = clientSetting.mqttMyIdentifierFirst(),
                        userName = clientSetting.mqttMyNameFirst(),
                        password = clientSetting.mqttBrokerPasswordFirst().toByteArray(),
                    ),
                    lightBulbStore = lightBulbStore,
                    parentJob = serviceJob!!,
                    onOffConnect = this@MQTTService::onOffConnect
                )
            )
        }
        isConn = true
        this@MQTTService.notifyObservers(isConn)
        showNotification(isConn)
    }

    override fun onCreate() {
        super.onCreate()
        notificationUtil = NotificationUtil(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        middleClient.disConnectClient()
        isConn = false
        this@MQTTService.notifyObservers(isConn)
        showNotification(isConn)
        return super.onUnbind(intent)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(isRunning: Boolean) {
        NotificationManagerCompat.from(this@MQTTService).run {
            notify(ID_NOTIFICATION, notificationUtil.doNotification(isRunning))
        }
    }

    private fun onOffConnect(value: Boolean) {
        if (isConn != value) {
            notifyObservers(value)
            isConn = value
            showNotification(isConn)
        }
    }

    override fun onDestroy() {
        NotificationManagerCompat.from(this).cancel(ID_NOTIFICATION)
        super.onDestroy()
        cancelJob()
        notifyObservers(false)
        middleClient.stopClient()
        stopSelf()
    }

    companion object {
        const val ID_NOTIFICATION = 987
    }
}