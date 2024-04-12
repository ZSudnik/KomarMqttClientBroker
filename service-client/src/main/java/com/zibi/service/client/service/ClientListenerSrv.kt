package com.zibi.service.client.service

import com.zibi.mod.data_store.data.Topic
import com.zibi.mod.data_store.preferences.LightBulbStore
import io.zibi.komar.mclient.MqttClient
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ClientListenerSrv(
    private val mqttClient: MqttClient,
    private val lightBulbStore: LightBulbStore,
    private val errorConnect: (Boolean) -> Unit,
    ): IListener {

    override fun onConnected() {
        Log.i("-->onConnected Add subscribe")
        errorConnect(true)
            try {
                //topic: tasmota_%06X
                //group topic 1: cmnd/tasmotas/
                //full topic: cmnd/tasmota_41310D/
                //fullback topic: cmnd/tasmota_41310D-fb/
                //  "tele/tasmota_41310D/#"
                //  "cmnd/tasmota_41310D/"
                val listTopic = mutableListOf<String>()
                Topic.list.forEach { topic ->
                    listTopic.add("stat/${topic}/#")
                    listTopic.add("tele/${topic}/#")
                }
                mqttClient.subscribe(0, listTopic )
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    override fun onConnectFailed(e: Throwable) {
        Log.e("-->onConnectFailed : $e ${e.printStackTrace()}")
        errorConnect(false)
    }

    override fun onConnectLost(e: Throwable) {
        Log.e("-->onConnectLost : $e")
        errorConnect(false)
    }

    override fun onReconnectStart(cur: Int) {
        Log.i("-->onConnectLost : $cur")
    }

    override fun onMessageArrived(topic: String, s: String) {
        Log.e("-->ClientListenerSrv.onMessageArrived : $topic : $s")
        CoroutineScope(Dispatchers.IO).launch {
            lightBulbStore.setMessageArrived(topic, s)
        }
    }


}

