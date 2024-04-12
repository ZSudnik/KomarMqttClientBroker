package com.zibi.service.broker.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.zibi.mod.common.resources.StringResolver
import com.zibi.service.broker.R
import com.zibi.service.broker.service.MQTTService

class NotificationUtil(
    context: Context,
    stringResolver: StringResolver
)  {

    val notification: Notification

    init {
        // Create the NotificationChannel
        val name = stringResolver.getString(R.string.fg_channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH // .IMPORTANCE_DEFAULT
        val channel = NotificationChannel(FG_SERVICE_CHANNEL, name, importance)
        channel.description = stringResolver.getString(R.string.fg_channel_description)
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(
            context,
            FG_SERVICE_CHANNEL
        )
            .setSmallIcon(R.drawable.hub_spot_broker_red)
            .setContentTitle(stringResolver.getString(R.string.notification_mqtt_service_title))
            .setContentText(stringResolver.getString(R.string.notification_mqtt_service_content))
            .setColor(if (MQTTService.isBrokerRunning) context.getColor(R.color.RED) else context.getColor(R.color.BLUE_DARK))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent().setClassName(
                        context,
                        "com.zibi.app.ex.broker.MainActivity"
                    ),
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        notification = builder.build()
    }

    companion object {
        const val FG_SERVICE_CHANNEL = "forground_service_channel_broker"
    }
}
