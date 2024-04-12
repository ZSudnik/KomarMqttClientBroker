package com.zibi.mod.fragment.permission

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

@Composable
fun showPermissionNotification(){

    val activity = LocalContext.current as Activity

val permissionOpenDialog = remember { mutableStateOf(false) }
val rationalPermissionOpenDialog = remember { mutableStateOf(false) }

if (permissionOpenDialog.value) {
    ShowSettingDialog(openDialog = permissionOpenDialog)
}

var hasNotificationPermission by remember {
    mutableStateOf(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(activity,
            Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    } else true
    )
}

val launcher = rememberLauncherForActivityResult(
    contract = RequestPermission(),
    onResult = { isGranted ->
        if (!isGranted) {
            if (shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS)) {
                rationalPermissionOpenDialog.value = true
            } else {
                permissionOpenDialog.value = true
            }
        } else {
            hasNotificationPermission = isGranted
        }
    }
)
if (rationalPermissionOpenDialog.value) {
    ShowRationalPermissionDialog(openDialog = rationalPermissionOpenDialog) {
        rationalPermissionOpenDialog.value = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

    val context = LocalContext.current
Column(
modifier = Modifier.fillMaxSize(),
verticalArrangement = Arrangement.Center,
horizontalAlignment = Alignment.CenterHorizontally
) {
    Button(onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }) {
        Text(text = "Request permission")
    }
    Button(onClick = {
        if (hasNotificationPermission) {
            showNotification(context)
        }
    }) {
        Text(text = "Show notification")
    }
}
}


private fun showNotification(context: Context) {
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "12345"
    val description = "Test Notification"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.lightColor = Color.BLUE

        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }
    val notification = NotificationCompat.Builder(context, channelId)
//        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Hello Nilesh")
        .setContentText("Test Notification")
        .build()
    notificationManager.notify(1, notification)
}

@Composable
fun ShowSettingDialog(openDialog: MutableState<Boolean>) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Notification Permission")
            },
            text = {
                Text("Notification permission is required, Please allow notification permission from setting")
            },

            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val packageName = "com.zibi.module.ppp"
                            intent.data = Uri.parse("package:$packageName")
//                            startActivity(intent)
                        },
                    ) {
                        Text("Ok")
                    }
                }

            },
        )
    }
}

@Composable
fun ShowRationalPermissionDialog(openDialog: MutableState<Boolean>, onclick: () -> Unit) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Alert")
            },
            text = {
                Text("Notification permission is required, to show notification")
            },

            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        onClick = onclick,
                    ) {
                        Text("Ok")
                    }
                }

            },
        )
    }
}
