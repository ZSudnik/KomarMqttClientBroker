package io.zibi.komar.mclient

import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.utils.Log

class ClientListener(
    private val mqttClient: io.zibi.komar.mclient.MqttClient,
    ): IListener {

    override fun onConnected() {
        Log.i("-->onConnected Add subscribe")
            try {
                //topic: tasmota_%06X
                //group topic 1: cmnd/tasmotas/
                //full topic: cmnd/tasmota_41310D/
                //fullback topic: cmnd/tasmota_41310D-fb/
                //  "tele/tasmota_41310D/#"
                //  "cmnd/tasmota_41310D/"
                mqttClient.subscribe(0,
                    listOf("stat/tasmota_41310D/#","tele/tasmota_41310D/#") )
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    override fun onConnectFailed(e: Throwable) {
        Log.e("-->onConnectFailed : $e ${e.printStackTrace()}")
    }

    override fun onConnectLost(e: Throwable) {
        Log.e("-->onConnectLost : $e")
    }

    override fun onReconnectStart(cur: Int) {
        Log.i("-->onConnectLost : $cur")
    }

    override fun onMessageArrived(topic: String, s: String) {
        Log.e("-->onMessageArrived : $topic : $s")
    }


}

