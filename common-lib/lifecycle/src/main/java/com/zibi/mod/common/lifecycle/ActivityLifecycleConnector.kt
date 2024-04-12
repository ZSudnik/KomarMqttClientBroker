package com.zibi.mod.common.lifecycle

import androidx.activity.ComponentActivity

interface ActivityLifecycleConnector {

  fun connect(activity: ComponentActivity)
}
