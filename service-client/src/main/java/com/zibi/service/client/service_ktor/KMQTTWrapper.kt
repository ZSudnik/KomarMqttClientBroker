package com.zibi.service.client.service_ktor

import android.annotation.SuppressLint
import android.util.Log
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.service.client.service.ClientProperties
import io.zibi.komar.mclient.ktor.Mqtt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

//fun Application.module() {
//    configureMqtt()
//}

//fun Application.configureMqtt() {
//    // Installs the plugin to the server so that you can use it, won't work otherwise
//    install(Mqtt) {
//        initSubscriptions(topics = Topic.list)
//    }
//
//    // Allows to map function to different topics
//    routing {
//        topic(Topic.LivingRoom.light1) {
//            println(it)
//        }
//        topic(Topic.LivingRoom.light2) {
//            println(it)
//        }
//    }
//}


object KxMQTTWrapper {

    fun onPublishCommand(topic: String, message: String) {
        if (Mqtt.client.isConnected()) {
            val msgMap =
                message.replace("[{}\"]".toRegex(), "")
                    .split("(,(?=[^,]+:))".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .associate {
                        val (left, right) = it.split(":")
                        left to right
                    }
            msgMap.forEach { (key, value) ->
                Mqtt.client.publish(topic = "cmnd/${topic}/${key}", content = value)
            }
//            val ss = "{\"POWER\":\"ON\",\"Dimmer\":100,\"Color\":\"3152870000\",\"HSBColor\":\"217,64,53\",\"White\":0,\"CT\":253,\"Channel\":[19,32,53,0,0]}"
//            val ss = "{\"Dimmer\":53,\"Color\":\"3152870000\",\"White\":0,\"CT\":253}"
//            val ss = "{\"Dimmer\":100,\"HSBColor\":\"300,64,100\",\"White\":0,\"CT\":180}"
//            val ss = "{\"POWER\":\"ON\",\"Dimmer\":100,\"Color\":\"3152870000\",\"HSBColor\":\"217,64,53\",\"White\":0,\"CT\":253,\"Channel\":[19,32,53,0,0]}"
//            val ss= "100,64,100"
//            mqttClient.publish(topic = "cmnd/tasmota_41310D/HSBColor", content = ss)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun startClientAuto(
        prop: ClientProperties,
        lightBulbStore: LightBulbStore,
        onOffConnect: (Boolean) -> Unit,
        ) {
        Mqtt.client.reloadConfiguration(prop)
        Mqtt.client.addOnMessageArrived( lightBulbStore::setMessageArrived)
        Mqtt.client.addOnOffMonitorConnection(onOffConnect)
        Mqtt.client.connectAuto()
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun startClientOne(
        prop: ClientProperties,
        lightBulbStore: LightBulbStore,
        coroutineScope: CoroutineScope,
        onOffConnect: (Boolean) -> Unit,
    ): Boolean {
        Mqtt.client.reloadConfiguration(prop)
        Mqtt.client.addOnMessageArrived( lightBulbStore::setMessageArrived)
        Mqtt.client.addOnOffMonitorConnection(onOffConnect)
        return coroutineScope.async {
            try {
                Mqtt.client.connectOne()
                Mqtt.client.isConnected()
            } catch (e: Exception) {
                false
            }
        }.await()
    }

    fun stopClient() {
        try {
            Mqtt.client.shutDown()
        } catch (e: Exception) {
            Log.e("", e.message ?: "")
        }
    }

    fun disConnectClient() {
        try {
            Mqtt.client.disConnect()
        } catch (e: Exception) {
            Log.e("", e.message ?: "")
        }
    }
}