package com.zibi.client.fragment.start.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zibi.client.fragment.start.data.AllLight
import com.zibi.client.fragment.start.data.LightPoint
import com.zibi.mod.common.bms.lightbulb.LightBulbColor
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.client.fragment.start.main.model.StartMainData
import com.zibi.client.fragment.start.utils.provideLastStoreState
import com.zibi.mod.common.ui.R
import com.zibi.client.fragment.start.utils.provideLightBulbColorPickerScreen
import com.zibi.common.device.lightbulb.LightBulbData

@Composable
fun StartMainScreen(viewModel: StartMainViewModel) {
    when (val state = viewModel.rememberState().value) {
        is StartMainState.LightBulbInit -> {
            LightBulbColorPickerScreen(
                data = viewModel.uiData(state.lightPoint),
                lastStateStore = viewModel.mapSnapshot,
                goFragmentSetting = viewModel::goToFragment
                )
        }
        else -> {}
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter",
    "UnrememberedMutableState"
)
@Composable
fun LightBulbColorPickerScreen(
        data: StartMainData.Initialized,
        lastStateStore: MutableMap<String,LightBulbData>,
        goFragmentSetting: (action: StartMainAction) -> Unit,
    ) {
    val stateLightPoint: LightPoint = data.lightPoint
    val changeEndPoint by rememberUpdatedState(data.changeEndPoint)
    val stateClient by remember {  data.stateClient }
    val menuExpanded = mutableStateOf(false)
    val context = LocalContext.current
    val colorPassive = AppTheme.colors.blue900
    val colorActive = AppTheme.colors.red900
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box {
                TopMenu(
                    title = data.lightPoint.desc,
                    iconMainResId = R.drawable.common_ui_ic_hamburger,
                    onIconMainClick = { menuExpanded.value = !menuExpanded.value },
                    iconMenuResId = R.drawable.common_ui_ic_refresh,
                    onIconMenuClick = { data.runStopServer(context) },
                    isIconMenuRotate = stateClient,
                    iconMenuColorActive = colorActive,
                    iconMenuColorPassive = colorPassive,
                )
                DropdownMenu(
                    expanded = menuExpanded.value,
                    onDismissRequest = {
                        menuExpanded.value = false
                    },
                ) {
                    DropdownMenuItem (onClick = {goFragmentSetting(StartMainAction.GoToSetting)}) {
                        Text(
                            text = "Setting",
                            style = AppTheme.typography.bodyMedium,
                        )
                    }
                    DropdownMenuItem (onClick = {goFragmentSetting(StartMainAction.GoToLivingRoom)}) {
                        Text(
                            text = "Living room",
                            style = AppTheme.typography.bodyMedium,
                        )
                    }
                    Divider(thickness = 1.dp, color = AppTheme.colors.red900)
                    AllLight().entries.forEach { light ->
                        ButtonMenu(lightPoint = light, stateLightPoint = stateLightPoint)
                                      { changeEndPoint(light) }
                    }

                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val listDataState = lastStateStore.flatMap { listOf( it.value) }
            LightBulbColor(
                listData =
                listDataState
                    .ifEmpty {
                    data.lightPoint.topics.map {
                        LightBulbData(topic = it, dataStr =  "{\"POWER\":\"OFF\",\"HSBColor\":\"217,64,53\"}")
                    }
                }
                ,
                onChangeListOfLightBulb = data.sendDataListOfLightBulb
            )
        }
    }
}

@Composable
fun ButtonMenu(lightPoint: LightPoint, stateLightPoint: LightPoint, onClick: ()->Unit){
    if(lightPoint == stateLightPoint) return
    DropdownMenuItem (onClick = onClick) {
        Text(
            text = lightPoint.desc,
            style = AppTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
fun StartFragmentPreview() {
    LightBulbColorPickerScreen(
        data = provideLightBulbColorPickerScreen(),
        lastStateStore = provideLastStoreState,
        goFragmentSetting = {}
    )
}


//@Composable
//fun LightBulbColorPickerScreen(data: StartMainData.Initialized,) {
////    val stateServer by remember { data.stateServer }
//    val context = LocalContext.current
//    val colorPassive = AppTheme.colors.blue900
//    val colorActive = AppTheme.colors.red900
//    Column {
//        TopAppBar(
//            title = {data.topMenuTitle},
////            navigationIcon = {
////                R.drawable.coi_common_ui_ic_refresh,
////            }
//        )
//        LightBulbColor(
//                listData = data.listBulb
//        )
//    }
//}
