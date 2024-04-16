package com.zibi.service.client.service

import com.zibi.mod.data_store.data.Topic
import com.zibi.mod.data_store.preferences.LightBulbStore
import io.zibi.komar.mclient.MqttClient
import io.zibi.komar.mclient.core.IListener
import io.zibi.komar.mclient.utils.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientListenerSrv(
    private val mqttClient: MqttClient,
    private val lightBulbStore: LightBulbStore,
    private val errorConnect: (Boolean) -> Unit,
) : IListener {

    override fun onConnected() {
        Log.i("onConnected add subscribe")
        errorConnect(true)

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
        mqttClient.subscribe(0, listTopic)
    }

    override fun onSubscribe(topic: String) {
        Log.i("onSubscribe topics: $topic")
    }

    override fun onUnsubscribe(topic: String) {
        Log.i("onUnsubscribe topics: $topic")
    }

    override fun onConnectFailed(e: Throwable) {
        Log.e("onConnectFailed : ${e.message} /n${e.printStackTrace()}")
    }

    override fun onConnectLost(e: Throwable) {
        Log.e("onConnectLost : ${e.message} /n${e.printStackTrace()}")
        errorConnect(false)

    }

    override fun onDisConnected(description: String) {
        Log.e("onDisConnected : $description")
        errorConnect(false)
    }

    override fun onMessageArrived(topic: String, s: String) {
//        Log.i("onMessageArrived : $topic : $s")
        CoroutineScope(Dispatchers.IO).launch {
            lightBulbStore.setMessageArrived(topic, s)
        }
    }

    override fun onResponseTimeout(description: String) {
        Log.e("onResponseTimeout : $description")
    }


}

