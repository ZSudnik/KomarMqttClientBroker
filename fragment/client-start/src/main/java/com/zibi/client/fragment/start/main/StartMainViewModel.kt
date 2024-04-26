package com.zibi.client.fragment.start.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import com.zibi.client.fragment.start.data.LightPoint
import kotlinx.coroutines.flow.Flow
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.client.fragment.start.main.model.StartMainData
import com.zibi.common.device.lightbulb.LightBulbData
import com.zibi.service.client.service.MQTTService
//import com.zibi.service.client.service_ktor.KMQTTService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface StartMainViewModel {
    val uiData: (lightPoint: LightPoint) -> StartMainData.Initialized
    val mapSnapshot: SnapshotStateMap<String,LightBulbData>
    fun goToFragment(action: StartMainAction)
    @Composable
    fun rememberState(): State<StartMainState?>
}

interface StartMainNavigation {
    sealed interface NavEvent {
        data object GoToFragmentMonitor : NavEvent
        data object GoToFragmentSetting : NavEvent
    }
    val navEvent: Flow<NavEvent>
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class StartMainViewModelImpl (
    private val stateMachine: StartMainStateMachine,
    private val lightBulbStore: LightBulbStore,
) : AbsStateViewModel<StartMainState, StartMainAction>(stateMachine),
    StartMainViewModel, StartMainNavigation {

    private var job: Job? = null

    @Composable
    override fun rememberState(): State<StartMainState?> = super.remState()

    override fun goToFragment(action: StartMainAction){
        super.dispatch(action = action )
    }

    override val mapSnapshot: SnapshotStateMap<String,LightBulbData> = mutableStateMapOf()
    override val uiData: (lightPoint: LightPoint) -> StartMainData.Initialized
        get() = {
            if(job == null || job?.isCancelled == true) {
                mapSnapshot.clear()
                job = viewModelScope.launch {
                    lightBulbStore.sendStoreMessageX(it.topics).collect { listDataPair ->
                        listDataPair.forEach { dataPair ->
                            mapSnapshot[dataPair.topic] = LightBulbData(dataPair.topic, dataPair.msg)
                        }
                    }
                }
            }
            StartMainData.Initialized(
                stateClient = MQTTService.isClientRunning,
                lightPoint = it,
                runStopServer = {context ->
                    MQTTService.onChangeConnection(context)
                },
                sendDataListOfLightBulb = { listBulb ->
                    listBulb.forEach { bulb ->
                        MQTTService.publish(
                            topic = bulb.topic,
                            message = bulb.toJsonString(),
                        )
                    }
                },
                changeEndPoint = { endPoint ->
                    job?.cancel()
                    super.dispatch(action = StartMainAction.GoToLightBulb(endPoint)  )
                },
           )
        }

    override val navEvent: Flow<StartMainNavigation.NavEvent>
        get() = stateMachine.navEvent

}

