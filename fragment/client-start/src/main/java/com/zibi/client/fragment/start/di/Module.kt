package com.zibi.client.fragment.start.di

import com.zibi.client.fragment.start.login.StartLoginStateMachine
import com.zibi.client.fragment.start.login.StartLoginViewModelImpl
import com.zibi.client.fragment.start.main.StartMainStateMachine
import com.zibi.client.fragment.start.main.StartMainViewModelImpl
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