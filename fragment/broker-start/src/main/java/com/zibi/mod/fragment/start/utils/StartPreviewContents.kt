package com.zibi.mod.fragment.start.utils

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.zibi.mod.fragment.start.login.model.StartLoginData
import com.zibi.mod.fragment.start.main.model.StartMainData
import kotlinx.coroutines.flow.MutableStateFlow

fun providePreviewStartLoginData(): StartLoginData.Screen =
    StartLoginData.Screen(
        topMenuTitle = "Login",
        hintUser = "Enter user",
        labelUser = "User",
        hintPassword = "Enter password",
        labelPassword = "Password",
        bottomButtonText = "Login",
        onBottomNavigationButton = { _, _ -> },
        dialogTitle = "Bad login",
        dialogContent = "User name or password is wrong. Try again",
        dialogButtonLabel = "OK",
        onDialogButtonClicked =  {},
    )


fun providePreviewStartMainData(): StartMainData.Initialized =
    StartMainData.Initialized(
        topMenuTitle = "Zibi Mqtt Broker",
        descState = "State",
        valueStateRun = "Running",
        valueStateStop = "Stop",
        stateServer =  mutableStateOf(true),
        descIPAddress = "IP Address",
        valueIPAddress = "192.168.1.125",
        descNumberClient = "Clients connected",
        valueNumberClient = mutableIntStateOf( 0),
        descLogs = "Logs",
        runStopServer = {},
        onGoToSetting = {},
        onEraserLogList = {},
    )
