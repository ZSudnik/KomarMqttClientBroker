package com.zibi.mod.fragment.broker.main

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.dialog.small.DecisionDialog
import com.zibi.mod.common.ui.dialog.small.SmallDialogType
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.inputs.InputField
import com.zibi.mod.common.ui.inputs.InputType
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.mod.common.ui.topMenu.model.TopMenuTitleSpanData
import com.zibi.mod.fragment.broker.domain.model.ParamApp
import com.zibi.mod.fragment.broker.domain.model.ParamBroker
import com.zibi.mod.fragment.broker.domain.model.SettingData
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine.State


@Composable
fun SettingMainScreen(viewModel: OneFragmentMainViewModel) {
    when (val state = viewModel.rememberState().value) {
        is State.ContentState -> SettingContextScreen(
            data = viewModel.uiData(),
            paramApp = state.paramApp,
            paramBroker = state.paramBroker,
            paramDefaultBroker = state.paramDefaultBroker,
        )
        else -> {}
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingContextScreen(
    data: SettingData,
    paramApp: ParamApp,
    paramBroker: ParamBroker,
    paramDefaultBroker: ParamBroker,
) {
    var showDialog by remember { mutableStateOf( false) }
    val newParamBroker = ParamBroker()
    val newParamApp = ParamApp()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopMenu(
                title = data.topMenuTitle,
                titleSpanData = TopMenuTitleSpanData(
                    color = AppTheme.colors.topMenuTitlePrefix,
                    range = 0..0,
                ),
                iconMainResId = R.drawable.common_ui_ic_arrow_left,
                onIconMainClick = { showDialog = true },
            )
        },
    ) {
        val context = LocalContext.current
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(AppTheme.colors.background)
                    .padding(
                        start = AppTheme.dimensions.mediumPadding,
                        end = AppTheme.dimensions.mediumPadding
                    )
                    .verticalScroll(rememberScrollState())
                    .weight(weight = 1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SpacerWithText(data.sectionBrokerTitle)
                newParamBroker.userName = ParameterText(
                    data.descBrokerUserName,
                    paramBroker.userName,
                    paramDefaultBroker.userName
                )
                newParamBroker.password = ParameterText(
                    data.descBrokerPassword,
                    paramBroker.password,
                    paramDefaultBroker.password
                )
                newParamBroker.mqttPort = ParameterInt(
                    data.descBrokerMqttPort,
                    paramBroker.mqttPort, paramDefaultBroker.mqttPort,
                    ::checkNumberPort, context, data.textPortWarning
                )
                newParamBroker.webSocketEnabled = ParameterBoolean(
                    data.descBrokerWebSocketEnabled,
                    paramBroker.webSocketEnabled,
                    paramDefaultBroker.webSocketEnabled
                )
                newParamBroker.webSocketPort = ParameterInt(
                    data.descBrokerWebSocketPath,
                    paramBroker.webSocketPort, paramDefaultBroker.webSocketPort,
                    ::checkNumberPort, context, data.textPortWarning
                )
                newParamBroker.webSocketPath = ParameterText(
                    data.descBrokerWebSocketPath,
                    paramBroker.webSocketPath,
                    paramDefaultBroker.webSocketPath,
                )
                newParamBroker.authenticationEnabled = ParameterBoolean(
                    data.descBrokerAuthenticationEnabled,
                    paramBroker.authenticationEnabled,
                    paramDefaultBroker.authenticationEnabled
                )
                SpacerWithText(data.sectionAppTitle)
                newParamApp.userName = ParameterText(data.descAppUserName, paramApp.userName, "")
                newParamApp.password = ParameterText(data.descAppPassword, paramApp.password, "")
            }
        }
        if (showDialog){
            val isParamEqualBroker = paramBroker == newParamBroker
            val isParamEqualApp = paramApp == newParamApp
            if(isParamEqualBroker && isParamEqualApp){
                data.onBack( null, null)
            }else{
                DecisionDialog(
                    dialog = SmallDialogType.Default(
                    title = data.dialogTitle,
                    content = data.dialogContent,
                        positiveButtonText = data.dialogPositiveButtonLabel,
                        negativeButtonText = data.dialogNegativeButtonLabel,
                        cancelButtonText = data.dialogCancelButtonLabel,
                        onPositiveButtonClick = {
                            data.onBack(
                                if(isParamEqualApp) null else newParamApp,
                                if(isParamEqualBroker) null else newParamBroker
                            )
                        },
                        onNegativeButtonClick = {data.onBack( null, null)},
                        onCancelButtonClick = {showDialog = false},
                    ),
                    onDismissRequest = {},
                )
            }
        }
    }

    BackHandler {
        showDialog = true
    }
}

@Composable
fun ParameterText(description: String, value: String, defaultValue: String): String {
    var valueLoc by remember { mutableStateOf(value) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.dimensions.regularPadding)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            InputField(
                label = description,
                hint = valueLoc,
                content = valueLoc,
                onValueChanged = { value ->
                    valueLoc = value
                }
            )
        }
        IconRefresh(onClick = { valueLoc = defaultValue })
    }
    Spacer(modifier = Modifier.height(3.dp))
    Divider()
    return valueLoc
}

@Composable
fun ParameterInt(
    description: String, value: Int, defaultValue: Int,
    warningToast: (String) -> Boolean, context: Context, textToast: String
): Int {
    var valueLoc by remember { mutableIntStateOf(value) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.dimensions.regularPadding)
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            InputField(
                type = InputType.Number(),
                label = description,
                hint = valueLoc.toString(),
                content = valueLoc.toString(),
                onValueChanged = { value ->
                    valueLoc = value.toInt()
                    if (!warningToast(value)) {
                        Toast.makeText(
                            context,
                            textToast,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
        IconRefresh(onClick = { valueLoc = defaultValue })
    }
    Spacer(modifier = Modifier.height(3.dp))
    Divider()
    return valueLoc
}

@Composable
fun ParameterBoolean(description: String, checked: Boolean, defaultValue: Boolean): Boolean {
    var checkedLoc by remember { mutableStateOf(value = checked) }
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.dimensions.regularPadding)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = description,
        )
        Switch(
            checked = checkedLoc,
            onCheckedChange = { checkedLoc = !checkedLoc },//onCheckedChange,
            interactionSource = interactionSource,
        )
        IconRefresh(isPadding = false, onClick = { checkedLoc = defaultValue })
    }
    Spacer(modifier = Modifier.height(3.dp))
    Divider()
    return checkedLoc
}

@Composable
fun IconRefresh(isPadding: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = if (isPadding) AppTheme.dimensions.xxLargePadding else 0.dp)
    ) {
        IconButton(
            modifier = Modifier.size(48.dp),
            onClick = onClick,
        ) {
            CustomIcon(
                iconResId = R.drawable.common_ui_ic_refresh_default,
                iconSize = IconSize.Medium,
            )
        }
    }
}

fun checkNumberPort(value: String): Boolean {
    val port: Int
    try {
        port = value.toInt()
        if (port < 1024) {
            return false
        }
    } catch (e: NumberFormatException) {
        return false
    }
    return true
}

@Composable
fun SpacerWithText(sectionTitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(modifier = Modifier.padding(horizontal = AppTheme.dimensions.xSmallPadding),
            text = sectionTitle
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimensions.xSmallPadding)
                .background(color = Color.Cyan)
                .align(alignment = Alignment.CenterVertically)
        )
    }
}


@Preview
@Composable
fun SettingContextScreenEmptyPreview() {
//    SettingContextScreen(providePreviewEmptyOneFragmentScreenData())
}

