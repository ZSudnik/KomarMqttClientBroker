package com.zibi.mod.common.bms.lightbulb

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.zibi.common.device.lightbulb.LightBulbData
import com.zibi.common.device.lightbulb.Power
import com.zibi.mod.common.bms.R
import com.zibi.mod.common.ui.button.nostate.ButtonSet
import com.zibi.mod.common.ui.button.nostate.ButtonSmallWithIconNoState
import com.zibi.mod.common.ui.button.nostate.ButtonState
import com.zibi.mod.common.ui.button.nostate.DuoToggleNoState
import com.zibi.mod.common.ui.colorpicker.HsvColor
import com.zibi.mod.common.ui.theme.AppTheme

val YellowLight = Color(red = 255, green = 223, blue = 22, alpha = 255)

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LightBulbColor(
    modifier: Modifier = Modifier,
    listData: List<LightBulbData> = emptyList(),
    onChangeListOfLightBulb: (listBulb: List<LightBulbData>) -> Unit,
) {
    val firsActiveLightBulb = listData.firstOrNull{ it.POWER.isON() } ?:
            listData.firstOrNull() ?: LightBulbData()
    val updatedStateListLightBulb by rememberUpdatedState(onChangeListOfLightBulb)
    val standardButtonSet = ButtonSet(
        activeIconResId = R.drawable.common_bms_node_lightbulb_outline,
        passiveIconResId = R.drawable.common_bms_node_lightbulb_off_outline,
        iconColor = AppTheme.colors.white,
        activeColor = Color.Red,
        passiveColor = Color.LightGray,
        state = ButtonState.ON,
    )

//    HsvColor.from(firsLightBulb.Color.getColor(YellowLight))
    var currentColor by mutableStateOf(firsActiveLightBulb.HSBColor)
    var extraColors by remember { mutableStateOf(emptyList<HsvColor>()) }
///////////////
    val listColor =  listData.map { mutableStateOf( it.HSBColor) }
    val listSmallButtonState = listData.map { mutableStateOf( ButtonState.getState(it.POWER.key)) }
    fun stateSmallButton(): ButtonState {
        var isON = true
        var isOFF = true
        listSmallButtonState.forEach {
            if(  it.value.isON() ) isOFF = false else isON = false
        }
        return when{
            isON -> ButtonState.ON
            isOFF -> ButtonState.OFF
            else -> ButtonState.MID
        }
    }
    //// /////////////
    var mainButtonState by mutableStateOf( stateSmallButton())
    val onSelectionChange = { state: ButtonState ->
        mainButtonState = state
        listSmallButtonState.map { it.value = state }
        updatedStateListLightBulb(
            listData.map {
                it.apply {
                    this.POWER = Power.getState( state.key)
                }
            }
        )
    }

    fun stateOnOff( buttonState: ButtonState, lightBulbData: LightBulbData) : ButtonState {
        val newState = if( buttonState.isON() ) ButtonState.OFF else ButtonState.ON
        updatedStateListLightBulb(
            listOf(
                lightBulbData.apply {
                    this.POWER = Power.getState( newState.key)
                }
            )
        )
        return newState
    }


    Column( modifier = modifier
        .padding(horizontal = AppTheme.dimensions.xxMediumPadding)) {
        Divider( modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.dimensions.xSmallPadding)
            .background(color = Color.Cyan)
            .padding(horizontal = AppTheme.dimensions.regularPadding)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.dimensions.largePadding,
                    vertical = AppTheme.dimensions.regularPadding),
            horizontalArrangement = Arrangement.Center,
        ) {
            DuoToggleNoState(
                state = mainButtonState,
                listButton = listOf(
                    ButtonSet(
                        activeIconResId = R.drawable.common_bms_node_lightbulb_group_outline,
                        iconColor = AppTheme.colors.white,
                        activeColor = Color.Red,
                        passiveColor = Color.LightGray,
                        state = ButtonState.ON,
                    ),
                    ButtonSet(
                        activeIconResId = R.drawable.common_bms_node_lightbulb_group_off_outline,
                        iconColor = AppTheme.colors.white,
                        activeColor = Color.Red,
                        passiveColor = Color.LightGray,
                        state = ButtonState.OFF,
                    ),
                ),
                onSelectionChange = onSelectionChange
            )
        }
        if(listData.size > 1) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppTheme.dimensions.largePadding,
                        vertical = AppTheme.dimensions.regularPadding
                    ),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 6,
            ) {
                listSmallButtonState.fastForEachIndexed { i, smallButtonState ->
                    ButtonSmallWithIconNoState(
                        buttonSet = standardButtonSet,
                        state = smallButtonState.value,
                        onClick = {
                            smallButtonState.value = stateOnOff(smallButtonState.value, listData[i])
                            mainButtonState = stateSmallButton()
                        },
                        bulbColor = listColor[i].value.toColor(),
                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
        }
        BaseColorPicker(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.8f),
            hsvColor = currentColor,
            isColorOrWhite = firsActiveLightBulb.isColorOrWhite,
            onColorChanged =  { color ->
                currentColor = color
                extraColors = color.getColors()
                updatedStateListLightBulb(
                    listData.mapIndexed { index, lightBulbData ->
                        if(listSmallButtonState[index].value.isON() ){
                            listColor[index].value = color
                            lightBulbData.HSBColor = color
                            lightBulbData
                        } else null
                    }.filterNotNull()
                )
            }
        )
    }
}


@Preview
@Composable
fun PreviewScreen(){
//    LightBulbColor(onChangeListOfLightBulb = {})
}
