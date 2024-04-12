package io.zibi.komar.mclient.example

import io.zibi.komar.mclient.MqttClient
import io.zibi.codec.mqtt.util.MqttConnectOptions
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object App {

    fun main( coroutineScope: CoroutineScope) {
        Log.enablePing(true)
        val options = MqttConnectOptions()

        // emqx
        options.host = "broker-cn.emqx.io"
        options.port = 1883

        // apollo
//        options.setHost("localhost");
//        options.setPort(61613);
//        options.setUserName("admin");
//        options.setPassword("password".getBytes(StandardCharsets.UTF_8));

        // anoah
//        options.setHost("localhost");
//        options.setPort(30380);
//        options.setUserName("anoah");
//        options.setPassword("uclass2019".getBytes(StandardCharsets.UTF_8));
        options.clientIdentifier = "Zibi0"
        options.keepAliveTime = 5
        options.isCleanSession = true
        val mqttClient = MqttClient(options, coroutineScope.coroutineContext) { value -> errorClient(value) }

        mqttClient.listener = object : IListener {
            override fun onConnected() {
                coroutineScope.launch {
                    try {
                        mqttClient.subscribe(1, listOf("topic111"))
                        mqttClient.publish("topic111", "hello, netty mqtt!")
//                    mqttClient.subscribe("topic1/#");
//                    mqttClient.publish("topic1/aaa", "hello, netty mqtt!-2-");
                        mqttClient.unsubscribe(listOf("topic111"))
                        mqttClient.close();
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onConnectFailed(e: Throwable) {}
            override fun onConnectLost(e: Throwable) {
                Log.i("-->onConnectLost : $e")
            }
            override fun onReconnectStart(cur: Int) {}
            override fun onMessageArrived(topic: String, s: String) {}
        }
        coroutineScope.launch {
            try {
                mqttClient.connectAuto()
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }
    }

    private fun errorClient(value: Boolean){
//        if(isConn != value) {
//            isClientRunning.value = value
//            isConn = value
//        }
    }
}