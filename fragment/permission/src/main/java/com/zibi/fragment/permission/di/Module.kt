package com.zibi.fragment.permission.di

import com.zibi.fragment.permission.main.PermissionStateMachine
import com.zibi.fragment.permission.main.PermissionViewModelImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


val fragmentPermissionModule = module{
    viewModelOf(::PermissionViewModelImpl)
    factoryOf(::PermissionStateMachine)
}