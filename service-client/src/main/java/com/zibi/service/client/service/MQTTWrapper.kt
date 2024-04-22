package com.zibi.service.client.service

import android.annotation.SuppressLint
import android.util.Log
import com.zibi.mod.data_store.preferences.LightBulbStore
import io.zibi.komar.mclient.MqttClient
import io.zibi.codec.mqtt.util.MqttConnectOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

object MQTTWrapper {
    private var mqttClient: MqttClient? = null

    fun onPublishCommand(topic: String, message: String) {
        if (mqttClient?.isConnected == true) {
            val msgMap =
                message.replace("[{}\"]".toRegex(), "")
                    .split("(,(?=[^,]+:))".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .associate {
                        val (left, right) = it.split(":")
                        left to right
                    }
            msgMap.forEach { (key, value) ->
                mqttClient?.publish(topic = "cmnd/${topic}/${key}", content = value)
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
    suspend fun startClientAuto(
        prop: ClientProperties,
        lightBulbStore: LightBulbStore,
        coroutineScope: CoroutineScope,
        errorConnect: (Boolean) -> Unit,
        ): Boolean {
        mqttClient = MqttClient(prop as MqttConnectOptions, coroutineScope.coroutineContext)
        mqttClient?.let {
            it.listener = ClientListenerSrv(
                mqttClient = it,
                lightBulbStore = lightBulbStore,
                errorConnect = errorConnect
            )
        }
        return coroutineScope.async {
             try {
                mqttClient?.connectAuto()
                mqttClient?.isConnected ?: false
            } catch (e: Exception) {
                false
            }
        }.await()
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun startClientOne(
        prop: ClientProperties,
        lightBulbStore: LightBulbStore,
        coroutineScope: CoroutineScope,
        errorConnect: (Boolean) -> Unit,
    ): Boolean {
        mqttClient = MqttClient(prop as MqttConnectOptions, coroutineScope.coroutineContext)
        mqttClient?.let {
            it.listener = ClientListenerSrv(
                mqttClient = it,
                lightBulbStore = lightBulbStore,
                errorConnect = errorConnect
            )
        }
        return coroutineScope.async {
            try {
                mqttClient?.connectOne()
                mqttClient?.isConnected ?: false
            } catch (e: Exception) {
                false
            }
        }.await()
    }

    fun isConnected() = ( mqttClient?.isConnected ?: false) && (mqttClient?.isSocketActive ?: false)

    fun stopClient() {
        try {
            mqttClient?.shutDown()
            mqttClient = null
        } catch (e: Exception) {
            Log.e("", e.message ?: "")
        }
    }

    fun disConnectClient() {
        try {
            mqttClient?.disConnect()
        } catch (e: Exception) {
            Log.e("", e.message ?: "")
        }
    }
}