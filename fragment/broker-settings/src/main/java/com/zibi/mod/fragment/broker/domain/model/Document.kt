package com.zibi.mod.fragment.broker.domain.model

import androidx.annotation.StringRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.zibi.mod.fragment.broker.main.SettingBrokerStateMachine

interface Document {
    val id: String
    @get:StringRes
    val name: Int
    @get:StringRes
    val description: Int?
        get() = null
    val icons: Icons
    val colors: Colors
    val action: SettingBrokerStateMachine.Action
//    val navigationEvent: AppGlobalNavigationEvent?
    val type: DocumentType
}


data class Icons(
    @DrawableRes val logo: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconOnList: Int? = null
)

data class Colors(
    @ColorRes val primary: Int,
    @ColorRes val secondary: Int,
)