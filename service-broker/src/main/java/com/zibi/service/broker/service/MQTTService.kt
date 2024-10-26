package com.zibi.service.broker.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat
import com.zibi.mod.data_store.preferences.BrokerSetting
import com.zibi.service.broker.log.LogStream
import com.zibi.service.broker.notification.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.concurrent.Volatile
import kotlin.coroutines.EmptyCoroutineContext


class MQTTService : Service() {

    private val brokerSetting: BrokerSetting by inject()
    private val logStream: LogStream by inject()
    private val notificationUtil: NotificationUtil by inject()

    private var serviceJob: Job = Job(parent = null)
    private val serviceScope= CoroutineScope(  Dispatchers.IO )
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(SERVICE_ID, notificationUtil.notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            START -> init()
            STOP -> stop()
        }
        return START_STICKY
    }

    private fun stop() {
        MQTTWrapper.stopBroker()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopSelf()
        } catch (e: Exception) {
            println("Service stopped without being started: ${e.message}")
        }
        isBrokerRunning = false
    }

    @SuppressLint("SuspiciousIndentation", "MissingPermission")
    private fun init() {
        if(isBrokerRunning) return
        isBrokerRunning = true
//        NotificationManagerCompat.from(this).run {
//            notify(SERVICE_ID, notificationUtil.notification)
//        }
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTTService::lock").apply {
                    acquire(1000L /*1 seconds*/)
                }
            }
        serviceJob = serviceScope.launch {
                MQTTWrapper.startBroker(
                    listener = MQTTListener( logStream),
                    brokerProperties = brokerSetting.getServerProperties())
            }
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
        const val SERVICE_ID = 987
        const val START = "start"
        const val STOP = "stop"
        var isBrokerRunning: Boolean = false

        fun start(context: Context) {
            val intent = Intent(context, MQTTService::class.java)
            intent.action = START
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, MQTTService::class.java)
            intent.action = STOP
            context.startService(intent)
        }
    }
}