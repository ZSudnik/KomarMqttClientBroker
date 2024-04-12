package com.zibi.fragment.permission.utils

import com.zibi.fragment.permission.model.PermissionData

fun providePermissionScreen(): PermissionData =
    PermissionData(
        textTitle = "Allow notification",
        textAsk = "Do you want to receive notifications?",
        textButton = "Enable Notifications"
    )
