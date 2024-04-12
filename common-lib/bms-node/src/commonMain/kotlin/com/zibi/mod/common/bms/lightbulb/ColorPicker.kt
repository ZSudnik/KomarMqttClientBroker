package com.zibi.mod.common.bms.lightbulb

import android.graphics.PointF
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.colorpicker.HsvColor
import com.zibi.mod.common.ui.colorpicker.harmony.SliderLightnessHSL
import com.zibi.mod.common.ui.colorpicker.harmony.colorForPosition
import com.zibi.mod.common.ui.colorpicker.toRadian
import com.zibi.mod.common.ui.switchcomponent.SwitchGradientComponent
import com.zibi.mod.common.ui.theme.AppTheme
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

@Composable
fun BaseColorPicker(
    modifier: Modifier = Modifier,
    hsvColor: HsvColor,
    isColorOrWhite: Boolean,
    onColorChanged: (HsvColor) -> Unit
) {
        Column(
            modifier = modifier
        ) {
            val updatedColor by rememberUpdatedState(hsvColor)
            val updatedOnValueChanged by rememberUpdatedState(onColorChanged)
            var isColorWhite by remember { mutableStateOf(isColorOrWhite) }
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                ColorPickerWithMagnifiers(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f),
                    hsvColor = updatedColor,
                    isColorOrWhite = isColorWhite,
                    onColorChanged = {
                        updatedOnValueChanged(it)
                    },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    SwitchGradientComponent(
                        checked = isColorWhite,
                        onCheckedChange = { change -> isColorWhite = change },
                        width = AppTheme.dimensions.switchXComponentWidth,
                        height = AppTheme.dimensions.switchXComponentHeight,
                        checkedThumbColor = listOf(Color.White, YellowLight),
                        uncheckedThumbColor = arrayOf(
                            HsvColor(0f, 1f, 1f, 1f),
                            HsvColor(60f, 1f, 1f, 1f),
                            HsvColor(120f, 1f, 1f, 1f),
                            HsvColor(180f, 1f, 1f, 1f),
                            HsvColor(240f, 1f, 1f, 1f),
                            HsvColor(300f, 1f, 1f, 1f),
                            HsvColor(360f, 1f, 1f, 1f)
                        ).map {
                            it.toColor()
                        },
                    )
                }
            }
            SliderLightnessHSL(
                modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 12.dp),
                lightness = updatedColor.value,
                onValueChange = { value ->
                    updatedOnValueChanged(updatedColor.copy(value = value))
                }
            )
        }
}

@Composable
private fun ColorPickerWithMagnifiers(
    modifier: Modifier = Modifier,
    hsvColor: HsvColor,
    isColorOrWhite: Boolean,
    onColorChanged: (HsvColor) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp)
            .wrapContentSize()
            .aspectRatio(1f, matchHeightConstraintsFirst = true)

    ) {
        val isColor by rememberUpdatedState(isColorOrWhite)
        val hsvColorUpdated by rememberUpdatedState(hsvColor)
        val updatedOnColorChanged by rememberUpdatedState(onColorChanged)

        val diameterPx by remember(constraints.maxWidth) {
            mutableIntStateOf(constraints.maxWidth)
        }

        var animateChanges by remember {
            mutableStateOf(false)
        }
        var currentlyChangingInput by remember {
            mutableStateOf(false)
        }
        var newPosition by remember {
            mutableStateOf(positionFor(isColorOrWhite,hsvColor,IntSize(diameterPx, diameterPx)))
//            mutableStateOf(Offset(diameterPx/2f, diameterPx/2f))
        }
        fun updateColorWheel(newPos: Offset, animate: Boolean) {
            // Work out if the new position is inside the circle we are drawing, and has a
            // valid color associated to it. If not, keep the current position
            val newColor =
                if(isColor)
                    colorForPosition(newPos, IntSize(diameterPx, diameterPx), hsvColorUpdated.value)
                else
                    whiteForPosition(newPos, IntSize(diameterPx, diameterPx), hsvColorUpdated.value)
            if (newColor != null) {
                animateChanges = animate
                updatedOnColorChanged(newColor)
                newPosition =  newPos
            }
        }

        val inputModifier = Modifier.pointerInput(diameterPx) {
            awaitEachGesture {
                    val down = awaitFirstDown(false)
                    currentlyChangingInput = true
                    updateColorWheel(down.position, animate = true)
                    drag(down.id) { change ->
                        updateColorWheel(change.position, animate = false)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                    currentlyChangingInput = false
                }
        }

        Box(modifier = inputModifier.fillMaxSize()
            .drawBehind {
                scale(scale = 1.05f) {
                    val cx = if(size.height > size.width) size.width else size.height
                    val al = 10
                    val angle = (-(120+2*al) *  Math.PI/180  ).toFloat()
                    val angleStart = ((120+al) *  Math.PI/180  ).toFloat()
                    val angleSweep = ((300-2*al) *  Math.PI/180  ).toFloat()
                    val r = cx.times(.5f)
                    val cx2 = cx.times( .5f)
                    val cx4 = cx.times( .25f)
                    val cx8 = cx.times( .125f)
//                    val cx16 = cx.times( .05f)
                    val x1=cx2 +(r * sin( angle ))
                    val x2=cx2 -(r * sin(angle))
                    val y1=cx2 -(r * cos(angle))

                    val path1 = Path()
                    path1.arcToRad(
                        Rect(Offset(0f, 0f), size = Size(cx,cx)),
                        angleStart,
                        angleSweep,
                        false
                    )
                    val path = Path().apply {
                        var point = PointF(x2, y1)
                        moveTo(point.x, point.y)
                        quadraticTo(
                            point.x.times(.868f),
                            point.y.times(1.1f),
                            point.x-cx8,
                            point.y+cx4
                        )
                        point = PointF(x1, y1)
                        moveTo(point.x, point.y)
                        quadraticTo(
                            point.x.times(1.53f),
                            point.y.times(1.1f),
                            point.x+cx8,
                            point.y+cx4
                        )
                    }
                    drawPath(
                        path = path1,
                        color = hsvColorUpdated.toColor(),
                        style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        ) {
            ColorWheel(
                hsvColor = hsvColor,
                diameter = diameterPx,
                isColorOrWhite = isColorOrWhite
            )
            HarmonyColorMagnifiers(
                diameterPx = diameterPx,
                hsvColor = hsvColor,
                animateChanges = animateChanges,
                currentlyChangingInput = currentlyChangingInput,
                offset = newPosition
            )
        }
    }
}

fun whiteForPosition(position: Offset, size: IntSize, value: Float): HsvColor? {
    val centerX: Double = size.width / 2.0
    val centerY: Double = size.height / 2.0
    val radius: Double = min(centerX, centerY)
    val xOffset: Double = position.x - centerX
    val yOffset: Double = position.y - centerY
    val centerOffset = hypot(xOffset, yOffset)
    val div = ( (radius-centerOffset)/radius ).toFloat()
    return if (centerOffset < radius) {
        val hsv = HsvColor.from(
            Color(
                red = (YellowLight.red * div),
                green = (YellowLight.green * div),
                blue = (YellowLight.blue * div),
                alpha = 1f
            )
        )
        HsvColor(
            hue = hsv.hue,
            saturation = (centerOffset / radius).toFloat(),
            value = value,
            alpha = 1.0f
        )
    } else {
        HsvColor.from(YellowLight)
    }
}

internal fun positionFor(isColorOrWhite: Boolean, color: HsvColor, size: IntSize): Offset{
    return if(isColorOrWhite)
        positionForColor(color, size)
    else
        positionForWhite(color, size)
}

internal fun positionForColor(color: HsvColor, size: IntSize): Offset {
    val radians = color.hue.toRadian()
    val phi = color.saturation
    val x: Float = ((phi * cos(radians)) + 1) / 2f
    val y: Float = ((phi * sin(radians)) + 1) / 2f
    return Offset(
        x = (x * size.width),
        y = (y * size.height)
    )
}
internal fun positionForWhite(color: HsvColor, size: IntSize): Offset {
    val radians = color.hue.toRadian()
    val phi = color.saturation
    val x: Float = ((phi * cos(radians)) + 1) / 2f
    val y: Float = ((phi * sin(radians)) + 1) / 2f
    return Offset(
        x = (x * size.width),
        y = (y * size.height)
    )
}
