package com.zibi.mod.fragment.broker.di

import com.zibi.mod.fragment.broker.main.SettingStateMachineImp
import com.zibi.mod.fragment.broker.main.SettingViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val fragmentSettingModule = module{
    viewModelOf(::SettingViewModelImpl)
    factoryOf(::SettingStateMachineImp)
}