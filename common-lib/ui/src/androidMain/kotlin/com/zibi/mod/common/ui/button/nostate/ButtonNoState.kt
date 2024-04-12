package com.zibi.mod.common.ui.button.nostate

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.utils.NoRippleInteractionSource

@Composable
fun ButtonSmallWithIconNoState(
    buttonSet: ButtonSet,
    state: ButtonState = ButtonState.ON,
    onClick: () -> Unit,
    bulbColor: Color = Color.Green,
) {
    Surface(modifier = Modifier
        .wrapContentSize(),
        shape = RoundedCornerShape(AppTheme.dimensions.xLargePadding),
        border = BorderStroke(5.dp, bulbColor),
    ) {
        Button(
            modifier = Modifier.size(AppTheme.dimensions.topMenuIconSize),
            shape = RoundedCornerShape(AppTheme.dimensions.xLargePadding),
            interactionSource = NoRippleInteractionSource(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (buttonSet.state == state) {
                    buttonSet.activeColor
                } else {
                    buttonSet.passiveColor
                }
            ),
            onClick = onClick,
            contentPadding = PaddingValues(AppTheme.dimensions.xSmallPadding),
            elevation = ButtonDefaults.elevation(
                defaultElevation = AppTheme.dimensions.xLargePadding,
                pressedElevation = AppTheme.dimensions.xLargePadding,
                disabledElevation = AppTheme.dimensions.xLargePadding
            ),
        ) {
            Icon(
                painter = painterResource(id = if (buttonSet.state == state) {
                    buttonSet.activeIconResId
                } else {
                    buttonSet.passiveIconResId ?: buttonSet.activeIconResId
                }
                ),
                tint = buttonSet.iconColor,
                modifier = Modifier.size(AppTheme.dimensions.inputIconSize),
                contentDescription = null
            )
        }
    }
}


@Composable
fun DuoToggleNoState(
    listButton: List<ButtonSet>,
    state: ButtonState = ButtonState.ON,
    onSelectionChange: (buttonState: ButtonState) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(20.dp))
        ) {
            listButton.forEach { buttonSet->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .clickable {
                            onSelectionChange(buttonSet.state)
                        }
                        .background(
                            if (buttonSet.state == state) {
                                buttonSet.activeColor
                            } else {
                                buttonSet.passiveColor
                            }
                        )
                        .padding(
                            vertical = 4.dp,
                            horizontal = 8.dp,
                        ),
                ){
                    Icon(
                        painter = painterResource(id = buttonSet.activeIconResId),
                        tint = buttonSet.iconColor,
                        modifier = Modifier
                            .size(AppTheme.dimensions.topMenuIconSize)
                            .align(Alignment.Center),
                        contentDescription = null
                    )
                }
            }
        }
    }
}