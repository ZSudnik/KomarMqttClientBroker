package com.zibi.mod.common.ui.colorpicker

/**
 * A representation of Color in Hue, Saturation and Value form.
 */
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

/**
 * A representation of Color in Hue, Saturation and Value form.
 */
object HsvColorExt{
        val Saver: Saver<HsvColor, *> = listSaver(
            save = {
                listOf(
                    it.hue,
                    it.saturation,
                    it.value,
                    it.alpha
                )
            },
            restore = {
                HsvColor(
                    it[0],
                    it[1],
                    it[2],
                    it[3]
                )
            }
        )

}
