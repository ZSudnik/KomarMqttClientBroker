package com.zibi.mod.fragment.start.di

import com.zibi.mod.fragment.start.login.StartLoginStateMachine
import com.zibi.mod.fragment.start.login.StartLoginViewModelImpl
import com.zibi.mod.fragment.start.main.StartMainStateMachine
import com.zibi.mod.fragment.start.main.StartMainViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val fragmentStartModule = module{
    viewModelOf(::StartLoginViewModelImpl)
    viewModelOf(::StartMainViewModelImpl)
    factoryOf(::StartLoginStateMachine)
    factoryOf(::StartMainStateMachine)
}