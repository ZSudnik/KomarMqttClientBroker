package com.zibi.mod.data_store.preferences

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import com.zibi.common.device.lightbulb.LightBulbShortModel
import com.zibi.mod.data_store.data.DataPair
import com.zibi.mod.data_store.data.Topic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class LightBulbStore(
    context: Context,
) {

    private val dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, STORE_STATE_LIGHTS)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(STORE_STATE_LIGHTS) }
        )

    private val mapStateTemplate: MutableMap<String,LightBulbShortModel> = mutableMapOf()
    init {
        Topic.list.forEach { topic ->
            mapStateTemplate[topic] = LightBulbShortModel(TEMPLATE)
        }
    }

    fun sendStoreMessageX(topics: List<String>): Flow<List<DataPair>> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { pref ->
            topics.map { topic ->
                val msg = pref[stringPreferencesKey(topic)] ?: TEMPLATE
                //must be block, another way send three times
//                if (mapBlockDuplicate[topic] == msg) null else {
//                    mapBlockDuplicate[topic] = msg
                    DataPair(topic, msg)
//                }
            }
        }
    }

     suspend fun setMessageArrived(topic: String, msg: String) {
        topicClean( topic)?.let { shorTopic ->
            mapStateTemplate[shorTopic]?.let { shortModel ->
                shortModel.overwrite(msg)
                dataStore.edit { it[stringPreferencesKey(shorTopic)] = shortModel.toJsonString() }
            }
        }
    }

    private fun topicClean(topicLong: String): String? {
        val listTopics = topicLong.split("/").toMutableList()
        return if (listTopics.size > 2 &&
            ((listTopics.first() == "tele" && listTopics.last() == "STATE") ||
            (listTopics.first() == "stat" && listTopics.last() == "RESULT"))
            ){
            listTopics.removeAt(0)
            listTopics.removeAt(listTopics.lastIndex)  //removeLast()
            listTopics.joinToString("/")
        } else null
    }

    companion object {
        private const val TEMPLATE = "{\"POWER\":\"OFF\",\"Dimmer\":100,\"HSBColor\":\"217,64,53\",\"White\":0,\"CT\":253}"
        private const val STORE_STATE_LIGHTS = "state_light_bulbs"
    }
}
