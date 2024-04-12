package com.zibi.mod.data_store.preferences

data class BrokerProperties(
    val mqttPort: Int,
    val wsEnabled: Boolean,
    val wsPort: Int,
    val wsPath: String,
    val authEnabled: Boolean,
    val userName: String,
    val password: String,
    val allowTasmota: Boolean,
)