package com.zibi.service.broker.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.zibi.mod.data_store.preferences.BrokerSetting
import com.zibi.service.broker.log.LogStream
import com.zibi.service.broker.notification.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MQTTService : Service() {

    private val brokerSetting: BrokerSetting by inject()
    private val logStream: LogStream by inject()
    private val notificationUtil: NotificationUtil by inject()

    private lateinit var serviceJob: Job
    private lateinit var scope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        serviceJob = SupervisorJob()
        scope = CoroutineScope(Dispatchers.IO + serviceJob)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> {
                init()
                return START_STICKY
            }
            STOP -> {
                stop()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    private fun stop() {
        isBrokerRunning = false
        MQTTWrapper.stopBroker()
//        stopForeground(true)
        stopSelf()
    }

    @SuppressLint("SuspiciousIndentation", "ForegroundServiceType")
    private fun init() {
        if (this::scope.isInitialized)
        scope.launch {
            MQTTWrapper.startBroker(
                listener = MQTTListener( logStream),
                brokerProperties = brokerSetting.getServerProperties())
        }
        isBrokerRunning = true
        startForeground(NOT_SERVICE_ID, notificationUtil.notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        MQTTWrapper.stopBroker()
        serviceJob.cancel()
    }

    companion object {
        const val NOT_SERVICE_ID = 987
        const val START = "start"
        const val STOP = "stop"
        var isBrokerRunning: Boolean = false


        fun start(context: Context) {
            val intent = Intent(context, MQTTService::class.java)
            intent.action = START
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, MQTTService::class.java)
            intent.action = STOP
            ContextCompat.startForegroundService(context, intent)
        }
    }
}