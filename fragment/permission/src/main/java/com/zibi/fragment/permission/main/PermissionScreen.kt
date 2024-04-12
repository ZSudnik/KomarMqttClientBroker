package com.zibi.fragment.permission.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.zibi.fragment.permission.model.PermissionData
import com.zibi.fragment.permission.utils.providePermissionScreen
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.theme.Color

@Composable
fun PermissionScreen(viewModel: PermissionViewModel) {
    when (val state = viewModel.rememberState().value) {
        is PermissionState.ContentState -> RequestPermissionScreen(
            data = state.data,
            goFragmentSetting = viewModel::goToFragment,
        )

        else -> {}
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RequestPermissionScreen(
    data: PermissionData,
    goFragmentSetting: (action: PermissionAction) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleObserver =
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val isPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) ==
                            PackageManager.PERMISSION_GRANTED
                    if (isPermission) {
                        goFragmentSetting(PermissionAction.GoToStart)
                    }
                }

                else -> Unit
            }
        }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }


    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                goFragmentSetting(PermissionAction.GoToStart)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
        }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.background),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = data.textTitle,
                    style = AppTheme.typography.modalTitle,
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = data.textAsk,
                        style = AppTheme.typography.subtitle,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = AppTheme.colors.green900
                    )
                    Button(
                        modifier = Modifier.padding(all = 10.dp),
                        onClick = {
                        permissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    }) {
                        Text(
                            text = data.textButton,
                            style = AppTheme.typography.subtitle,
                        )
                    }
                }
            }
        },
    )


//    Column(
//        modifier = Modifier.fillMaxSize().background(color = AppTheme.colors.background),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = data.textAsk,
//            style = AppTheme.typography.subtitle,
//            )
////        Spacer(modifier = Modifier.height(16.dp))
//        HorizontalDivider(
//            modifier = Modifier.padding(all = 16.dp),
//            thickness = 1.dp,
//            color = AppTheme.colors.green900
//            )
////        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = {
//            permissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
//        }) {
//            Text(
//                text = data.textButton,
//                style = AppTheme.typography.subtitle,
//                )
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun RequestPermissionScreenPreview() {
    RequestPermissionScreen(
        providePermissionScreen()
    ) {}
}


