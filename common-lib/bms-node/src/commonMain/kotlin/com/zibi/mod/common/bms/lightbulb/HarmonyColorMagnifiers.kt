package com.zibi.mod.common.bms.lightbulb

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.zibi.mod.common.ui.colorpicker.HsvColor
import com.zibi.mod.common.ui.colorpicker.harmony.Magnifier

@Composable
fun HarmonyColorMagnifiers(
    diameterPx: Int,
    hsvColor: HsvColor,
    animateChanges: Boolean,
    currentlyChangingInput: Boolean,
    offset: Offset = Offset(0f,0f)
) {
    val size = IntSize(diameterPx, diameterPx)
    val position = remember(hsvColor, size) {
            offset
    }

    val positionAnimated = remember {
        Animatable(position, typeConverter = Offset.VectorConverter)
    }
    LaunchedEffect(hsvColor, size, animateChanges) {
        if (!animateChanges) {
            positionAnimated.snapTo(offset)
        } else {
            positionAnimated.animateTo(
                offset,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
            )
        }
    }

    val diameterDp = with(LocalDensity.current) {
        diameterPx.toDp()
    }

    val animatedDiameter = animateDpAsState(
        targetValue = if (!currentlyChangingInput) {
            diameterDp * diameterMainColorDragging
        } else {
            diameterDp * diameterMainColor
        },
        label = ""
    )

    hsvColor.getColors().forEach { color ->
        val positionForColor = remember {
            Animatable(offset, typeConverter = Offset.VectorConverter)
        }
        LaunchedEffect(color, size, animateChanges) {
            if (!animateChanges) {
                positionForColor.snapTo(offset)
            } else {
                positionForColor.animateTo(
                    offset,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                )
            }
        }
        Magnifier(position = positionForColor.value, color = color, diameter = diameterDp * diameterHarmonyColor)
    }
    Magnifier(position = positionAnimated.value, color = hsvColor, diameter = animatedDiameter.value)
}

private const val diameterHarmonyColor = 0.10f
private const val diameterMainColorDragging = 0.18f
private const val diameterMainColor = 0.15f
