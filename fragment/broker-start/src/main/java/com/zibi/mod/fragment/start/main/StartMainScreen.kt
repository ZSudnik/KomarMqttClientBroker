package com.zibi.mod.fragment.start.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.topMenu.TopMenu
import com.zibi.mod.common.ui.topMenu.model.TopMenuTitleSpanData
import com.zibi.mod.fragment.start.main.model.StartMainData
import com.zibi.mod.common.ui.R
import com.zibi.mod.common.ui.icon.CustomIcon
import com.zibi.mod.common.ui.icon.IconSize
import com.zibi.mod.common.ui.logview.LogView
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource
import com.zibi.mod.fragment.start.utils.providePreviewStartMainData

@Composable
fun StartMainScreen(viewModel: StartMainViewModel) {
    when (viewModel.rememberState().value) {
        is StartMainState.Initialized -> StartMainScreenInitialized(
            data = viewModel.uiData(),
            logs = viewModel.logs,
        )
        else -> {}
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartMainScreenInitialized(
    data: StartMainData.Initialized,
    logs: List<String>,
) {
    val stateServer by remember { data.stateServer }
    val valueNumberClient by remember { data.valueNumberClient }
    val context = LocalContext.current
    val colorPassive = AppTheme.colors.blue900
    val colorActive = AppTheme.colors.red900
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopMenu(
                title = data.topMenuTitle,
                titleSpanData = TopMenuTitleSpanData(
                    color = AppTheme.colors.topMenuTitlePrefix,
                    range = 0..3,
                ),
                iconMainResId = R.drawable.common_ui_ic_settings,
                onIconMainClick = { data.onGoToSetting() },
                iconMenuResId = R.drawable.common_ui_ic_refresh,
                onIconMenuClick = { data.runStopServer( context) },
                isIconMenuRotate = stateServer,
                iconMenuColorActive = colorActive,
                iconMenuColorPassive = colorPassive,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.dimensions.xSmallPadding)
                    .background(color = Color.Cyan)
                    .padding(horizontal = AppTheme.dimensions.regularPadding),
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(AppTheme.colors.background)
                    .padding(horizontal = AppTheme.dimensions.mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
                ParameterText(
                    data.descState,
                    if (stateServer) data.valueStateRun else data.valueStateStop,
                    changeColor = stateServer,
                    colorActive = colorActive,
                    colorPassive = colorPassive
                )
                ParameterText(data.descIPAddress, data.valueIPAddress, weightFirstText = 0.8f)
                ParameterText(data.descNumberClient, valueNumberClient.toString(), weightFirstText = 2f)
                Row (modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = data.descLogs,
                        style = AppTheme.typography.bodyBold,
                    )
                    IconButton(
                        interactionSource = NoRippleInteractionSource(),
                        modifier = Modifier.size(48.dp),
                        onClick = data.onEraserLogList
                    ) {
                        CustomIcon(
                            iconResId = R.drawable.common_ui_ic_eraser,
                            iconSize = IconSize.Medium,
                        )
                    }
                }
                Divider()
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
                LogView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    logs = logs,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
                Divider()
                Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))

            }
        }
    }
}

@Composable
fun ParameterText(description: String, value: String, changeColor: Boolean = true,
                  colorActive: Color = Color.Black, colorPassive: Color = Color.Blue ,
                  weightFirstText: Float = 1f){
    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.mediumPadding)
        ) {
            Text(
                modifier = Modifier.weight( weightFirstText),
                text = description,
                style = AppTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = value,
                textAlign = TextAlign.Right,
                style = AppTheme.typography.bodyMedium,
                color = if (changeColor) colorActive else colorPassive,
            )
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        Divider()
    }
    }

@Preview
@Composable
fun StartFragmentPreview() {
    StartMainScreenInitialized(
        data = providePreviewStartMainData(),
        logs = emptyList()
    )
}