package com.zibi.mod.fragment.start.main

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.zibi.broker.fragment.start.R
import com.zibi.mod.common.resources.StringResolver
import com.zibi.mod.fragment.start.main.model.StartMainData
import com.zibi.service.broker.log.LogStream
import com.zibi.service.broker.log.MsgType
import com.zibi.service.broker.service.MQTTService
import com.zibi.service.broker.service.MQTTWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration

interface StartMainViewModel {
    val uiData: () -> StartMainData.Initialized
    var logs: SnapshotStateList<String>
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
    private val stringResolver: StringResolver,
    private val logStream: LogStream,
) : AbsStateViewModel<StartMainState, StartMainAction>(stateMachine),
    StartMainViewModel, StartMainNavigation {

    @Composable
    override fun rememberState(): State<StartMainState?> =
        super.remState()

    private var isServerRunning = mutableStateOf(MQTTService.isBrokerRunning)
    private var clientsCount = mutableIntStateOf(MQTTWrapper.clientsConnected)
    override var logs = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            logs.addAll( logStream.getAllLogs() )
            logStream.logFlow.collect { logData ->
                if (logData.msgType == MsgType.CONNECTION) {
                    clientsCount.intValue = MQTTWrapper.clientsConnected
                }
                logs.add(logData.msg)
            }
        }
    }

//    override fun update(isRun: Boolean) { isServerRunning.value = isRun }
//    override fun numberClient(numClient: Int) { clientsCount.intValue = numClient }
//    override fun loadListEvent(listStrEvent: List<String>) { logs.addAll( listStrEvent ) }
//    override fun addEvent(strEvent: String){ logs.add(strEvent)}

    override val uiData: () -> StartMainData.Initialized
        get() = {
            StartMainData.Initialized(
                topMenuTitle = stringResolver.getString(R.string.fragment_start_main_top_bar_title),
                descState = stringResolver.getString(R.string.fragment_start_main_desc_state),
                valueStateRun = stringResolver.getString( R.string.fragment_start_main_state_run),
                valueStateStop = stringResolver.getString(  R.string.fragment_start_main_state_stop),
                stateServer = isServerRunning,
                descIPAddress = stringResolver.getString(R.string.fragment_start_main_desc_ip_address),
                valueIPAddress = getLocalIpAddress() ?: "",
                descNumberClient = stringResolver.getString(R.string.fragment_start_main_desc_clients_connected),
                valueNumberClient = clientsCount,
                descLogs = stringResolver.getString(R.string.fragment_start_main_desc_logs),
                runStopServer = { context ->
                    if (isServerRunning.value) MQTTService.stop(context)
                    else MQTTService.start(context)
                    isServerRunning.value = !isServerRunning.value
                },
                onGoToSetting =  { super.dispatch(action = StartMainAction.GoToFragmentOne) },
                onEraserLogList = { logs.clear() }
            )
        }


    override val navEvent: Flow<StartMainNavigation.NavEvent>
        get() = stateMachine.navEvent

    private fun getLocalIpAddress(): String? {
        try {
            val nwInterfaceList: Enumeration<NetworkInterface> =
                NetworkInterface.getNetworkInterfaces()
            for (nwInterface in nwInterfaceList) {
                val inetAddressList = nwInterface.inetAddresses
                for (inetAddress in inetAddressList) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("NetworkUtil", ex.message ?: "")
        }
        return null
    }

}