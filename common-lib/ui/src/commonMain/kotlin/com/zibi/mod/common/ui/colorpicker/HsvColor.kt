package com.zibi.mod.common.ui.colorpicker

/**
 * A representation of Color in Hue, Saturation and Value form.
 */
import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.model.HSV
import com.github.ajalt.colormath.model.RGB

/**
 * A representation of Color in Hue, Saturation and Value form.
 */
data class HsvColor(

    // from = 0.0, to = 360.0
    val hue: Float,

    // from = 0.0, to = 1.0
    val saturation: Float,

    // from = 0.0, to = 1.0
    var value: Float,

    // from = 0.0, to = 1.0
    val alpha: Float
) {

    fun toColor(): Color {
        val hsv = HSV(hue, saturation, value, alpha)
        val rgb = hsv.toSRGB()
        return Color(rgb.redInt, rgb.greenInt, rgb.blueInt, rgb.alphaInt)
    }

    fun getComplementaryColor(): List<HsvColor> {
        return listOf(
            this.copy(saturation = (saturation + 0.1f).coerceAtMost(1f), value = (value + 0.3f).coerceIn(0.0f, 1f)),
            this.copy(saturation = (saturation - 0.1f).coerceAtMost(1f), value = (value - 0.3f).coerceIn(0.0f, 1f)),
            this.copy(hue = (hue + 180) % 360), // actual complementary
            this.copy(hue = (hue + 180) % 360, saturation = (saturation + 0.2f).coerceAtMost(1f), value = (value - 0.3f).coerceIn(0.0f, 1f))
        )
    }

    fun getSplitComplementaryColors(): List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 150) % 360, saturation = (saturation - 0.05f).coerceIn(0.0f, 1f), value = (value - 0.3f).coerceIn(0.0f, 1f)),
            this.copy(hue = (hue + 210) % 360, saturation = (saturation - 0.05f).coerceIn(0.0f, 1f), value = (value - 0.3f).coerceIn(0.0f, 1f)),
            this.copy(hue = (hue + 150) % 360), // actual
            this.copy(hue = (hue + 210) % 360) // actual
        )
    }

    fun getTriadicColors(): List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 120) % 360, saturation = (saturation - 0.05f).coerceIn(0.0f, 1f), value = (value - 0.3f).coerceIn(0.0f, 1f)),
            this.copy(hue = (hue + 120) % 360),
            this.copy(hue = (hue + 240) % 360, saturation = (saturation - 0.05f).coerceIn(0.0f, 1f), value = (value - 0.3f).coerceIn(0.0f, 1f)),
            this.copy(hue = (hue + 240) % 360)
        )
    }

    fun getTetradicColors(): List<HsvColor> {
        return listOf(
            this.copy(saturation = (saturation + 0.2f).coerceIn(0.0f, 1f)), // bonus one
            this.copy(hue = (hue + 90) % 360),
            this.copy(hue = (hue + 180) % 360),
            this.copy(hue = (hue + 270) % 360)
        )
    }

    fun getAnalagousColors(): List<HsvColor> {
        return listOf(
            this.copy(hue = (hue + 30) % 360),
            this.copy(hue = (hue + 60) % 360),
            this.copy(hue = (hue + 90) % 360),
            this.copy(hue = (hue + 120) % 360)
        )
    }

    fun getMonochromaticColors(): List<HsvColor> {
        return listOf(
            this.copy(saturation = (saturation + 0.2f).mod(1f)),
            this.copy(saturation = (saturation + 0.4f).mod(1f)),
            this.copy(saturation = (saturation + 0.6f).mod(1f)),
            this.copy(saturation = (saturation + 0.8f).mod(1f))
        )
    }

    fun getShadeColors(): List<HsvColor> {
        return listOf(
            this.copy(value = (value - 0.10f).mod(1.0f).coerceAtLeast(0.2f)),
            this.copy(value = (value + 0.55f).mod(1.0f).coerceAtLeast(0.55f)),
            this.copy(value = (value + 0.30f).mod(1.0f).coerceAtLeast(0.3f)),
            this.copy(value = (value + 0.05f).mod(1.0f).coerceAtLeast(0.2f))
        )
    }

    fun getColors(): List<HsvColor> = emptyList()

    fun toStrJson(): String {
        val husStr = if (this.hue.isNaN()) 0 else this.hue.toInt()
        val saturationStr = (100 * this.saturation).toInt().toString()
        val valueStr = (100 * this.value).toInt().toString()
        return "$husStr,$saturationStr,$valueStr"
    }

    companion object {

        val DEFAULT = HsvColor(360f, 1.0f, 1.0f, 1.0f)

        /**
         *  the color math hsv to local hsv color
         */
        private fun HSV.toColor(): HsvColor {
            return HsvColor(
                hue = if (this.h.isNaN()) 0f else this.h,
                saturation = this.s,
                value = this.v,
                alpha = this.alpha
            )
        }

        fun from(color: Color): HsvColor {
            return RGB(
                color.red,
                color.green,
                color.blue,
                color.alpha
            ).toHSV().toColor()
        }

        fun fromStrJson(hsv: String): HsvColor {
            return try {
                val hsvInt = hsv.split(",").map { it.toInt() }
                HsvColor(
                    hue = if (hsvInt[0] < 0) 0f else hsvInt[0].toFloat(),
                    saturation = hsvInt[1] / 100f,
                    value = hsvInt[2] / 100f,
                    alpha = 1.0f,
                )
            } catch (e: Exception) {
                e.printStackTrace()
                DEFAULT
            }
        }
    }
}
