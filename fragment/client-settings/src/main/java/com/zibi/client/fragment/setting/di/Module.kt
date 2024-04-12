package com.zibi.client.fragment.setting.di

import com.zibi.client.fragment.setting.main.SettingStateMachineImp
import com.zibi.client.fragment.setting.main.SettingViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val fragmentSettingModule = module{
    viewModelOf(::SettingViewModelImpl)
    factoryOf(::SettingStateMachineImp)
}