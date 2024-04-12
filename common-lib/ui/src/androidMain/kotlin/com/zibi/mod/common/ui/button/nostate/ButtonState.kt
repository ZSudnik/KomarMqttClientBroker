package com.zibi.mod.common.ui.button.nostate

import androidx.compose.ui.graphics.Color

enum class ButtonState(val key: String) {
    ON("ON"), OFF("OFF"), MID("MID");
    fun isON() = this == ON
    fun isOFF() = this == OFF
    companion object {
        fun getState(key: String): ButtonState = entries.find { it.key == key } ?: ON
    }
}

data class ButtonSet(
    val activeIconResId: Int,
    val passiveIconResId: Int? = null,
    val iconColor: Color,
    val state: ButtonState,
    val activeColor: Color,
    val passiveColor : Color,
    val text: String? = null,
    val textColor: Color = Color.Black,
)