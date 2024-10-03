package com.zibi.client.fragment.start.main

//import com.zibi.service.client.service_ktor.KMQTTService
import com.zibi.service.client.IClientService
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity.BIND_AUTO_CREATE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import com.zibi.client.fragment.start.data.LightPoint
import com.zibi.client.fragment.start.main.model.StartMainData
import com.zibi.common.device.lightbulb.LightBulbData
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.service.client.Observer
import com.zibi.service.client.service.MQTTService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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
    StartMainViewModel, StartMainNavigation, Observer {

    private var job: Job? = null

    @Composable
    override fun rememberState(): State<StartMainState?> = super.remState()

    override fun goToFragment(action: StartMainAction){
        super.dispatch(action = action )
    }
/////////////////////////////////////
    private val isClientRunning = mutableStateOf(false)
    private lateinit var clientService: IClientService
//    private var serviceBounded: Boolean = false

    /** Callbacks for service binding */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            serviceBounded = true
            clientService = IClientService.Stub.asInterface(service)
            clientService.addObserver( this@StartMainViewModelImpl)
            clientService.onConnected()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
//            serviceBounded = false
//            clientService.onChangeConnection()
        }
    }


    ////////////////
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
                stateClient = isClientRunning,
                lightPoint = it,
                runStopServer = {context ->
                    if(!isClientRunning.value) {
                        val intent = Intent(context, MQTTService::class.java)
                        context.bindService(intent, connection, BIND_AUTO_CREATE)
                    }else{
                        context.unbindService(connection)
                    }
                },
                sendDataListOfLightBulb = { listBulb ->
                    listBulb.forEach { bulb ->
                        clientService.publish(bulb.topic, bulb.toJsonString())
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

    override fun asBinder(): IBinder? {
        return null
    }

    override fun update(isRun: Boolean) {
        isClientRunning.value = isRun
    }

}

