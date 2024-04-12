package com.zibi.mod.common.ui.colorpicker.harmony

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zibi.mod.common.ui.colorpicker.HsvColor
import com.zibi.mod.common.ui.slider.ColorfulSlider
import com.zibi.mod.common.ui.slider.MaterialSliderDefaults
import com.zibi.mod.common.ui.slider.SliderBrushColor
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun BrightnessBar(
    modifier: Modifier = Modifier,
    currentColor: HsvColor,
    onValueChange: (Float) -> Unit,
    ) {
    Slider(
        modifier = modifier,
        value = currentColor.value,
        onValueChange = {
            onValueChange(it)
        },
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colors.primary,
            thumbColor = MaterialTheme.colors.primary
        )
    )
}



@Composable
private fun HSLSliderExamples(modifier: Modifier, sliderModifier: Modifier) {

//    var hue by remember { mutableFloatStateOf(0f) }
//    var saturation by remember { mutableFloatStateOf(.5f) }
    var lightness by remember { mutableFloatStateOf(.5f) }
//    var alpha by remember { mutableFloatStateOf(1f) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        SliderLightnessHSL(
            modifier = sliderModifier,
            lightness = lightness,
            onValueChange = { result ->
                lightness = result
            }
        )

    }
}


@Composable
fun SliderLightnessHSL(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 360.0) hue: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) saturation: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) lightness: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderLightnessGradient = sliderLightnessGradient(hue, saturation)

    CheckeredColorfulSlider(
        modifier = modifier,
        value = lightness,
        onValueChange = onValueChange,
        brush = sliderLightnessGradient
    )
}


fun sliderLightnessGradient(
    hue: Float,
    saturation: Float = 0f,
    alpha: Float = 1f,
    start: Offset = Offset.Zero,
    end: Offset = Offset.Infinite
): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.hsl(hue = hue, saturation = saturation, lightness = 0f, alpha = alpha),
            Color.hsl(hue = hue, saturation = saturation, lightness = .5f, alpha = alpha),
            Color.hsl(hue = hue, saturation = saturation, lightness = 1f, alpha = alpha)
        ),
        start = start,
        end = end
    )
}

@Composable
fun CheckeredColorfulSlider(
    modifier: Modifier = Modifier,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
    brush: Brush,
    drawChecker: Boolean = false
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        if (drawChecker) {
            Box(
                modifier = Modifier
                    .width(maxWidth)
                    .height(12.dp)
//                    .drawChecker(shape = RoundedCornerShape(6.dp))
            )
        }

        ColorfulSlider(
            value = value*value,
            modifier = Modifier,
            thumbRadius = 12.dp,
            trackHeight = 12.dp,
            onValueChange = { value ->
                onValueChange(value.toDouble().pow(0.5).toFloat() )
            },
            valueRange = valueRange,
            coerceThumbInTrack = true,
            colors = MaterialSliderDefaults.materialColors(
                activeTrackColor = SliderBrushColor(brush = brush),
                inactiveTrackColor = SliderBrushColor(color = Color.Transparent)
            ),
            drawInactiveTrack = false
        )
    }
}
