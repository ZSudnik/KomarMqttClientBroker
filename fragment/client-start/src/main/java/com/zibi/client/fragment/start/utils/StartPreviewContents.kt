package com.zibi.client.fragment.start.utils

import androidx.compose.runtime.mutableStateOf
import com.zibi.client.fragment.start.data.LightPoint
import com.zibi.client.fragment.start.login.model.StartLoginData
import com.zibi.client.fragment.start.main.model.StartMainData
import com.zibi.common.device.lightbulb.LightBulbData

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

//{"POWER":"ON","Dimmer":53,"Color":"3152870000","HSBColor":"217,64,53","White":0,"CT":153,"Channel":[19,32,53,0,0]}
//{"POWER":"ON","Dimmer":53,"Color":"0000000285","HSBColor":"217,64,0","White":53,"CT":493,"Channel":[0,0,0,1,52]}
fun provideLightBulbColorPickerScreen(): StartMainData.Initialized =
    StartMainData.Initialized(
        stateClient = mutableStateOf(true),
        lightPoint = LightPoint.LightOne,
        runStopServer = {},
        sendDataListOfLightBulb = {},
        changeEndPoint = {}
    )

val provideLastStoreState: MutableMap<String,LightBulbData> =
    mutableMapOf(LightPoint.LightOne.topics.first() to LightBulbData(LightPoint.LightOne.topics.first(),
        "{\"POWER\":\"OFF\",\"HSBColor\":\"217,64,53\"}")
    )

