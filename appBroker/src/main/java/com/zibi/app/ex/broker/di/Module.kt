package com.zibi.app.ex.broker.di

import com.zibi.app.ex.broker.view.fragment.AppManagerFragmentNavigator
import com.zibi.app.ex.broker.view.fragment.feature.StartFragment
import com.zibi.app.ex.broker.view.fragment.feature.SettingFragment
import com.zibi.mod.common.navigation.fragments.FragmentNavigator
import com.zibi.mod.common.navigation.global.GlobalNavigationManager
import com.zibi.mod.common.navigation.global.GlobalNavigationManagerImpl
import com.zibi.mod.common.navigation.intents.IntentManager
import com.zibi.mod.common.navigation.intents.IntentManagerImpl
import org.koin.androidx.fragment.dsl.fragmentOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module{
    fragmentOf(::StartFragment)
    fragmentOf(::SettingFragment)
    singleOf<GlobalNavigationManager>(::GlobalNavigationManagerImpl)
    singleOf<IntentManager> (::IntentManagerImpl )
//    single<FragmentNavigator> { AppManagerFragmentNavigator(get())}
//      single { IntentManagerConnector( get()) }
      }
