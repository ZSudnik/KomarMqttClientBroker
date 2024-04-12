package com.zibi.app.ex.broker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.zibi.app.ex.broker.di.appModule
import com.zibi.mod.common.navigation.navigationModule
import com.zibi.mod.common.resources.di.resourcesModule
import com.zibi.mod.data_store.di.dataStoreModule
import com.zibi.mod.fragment.broker.di.fragmentSettingModule
import com.zibi.mod.fragment.start.di.fragmentStartModule
import com.zibi.service.broker.di.serviceBrokerModule
import pl.gov.coi.common.storage.di.storageModule

import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BaseApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@BaseApplication)
      androidLogger()
      modules(
        listOf(
          appModule,
          fragmentStartModule,
          fragmentSettingModule,
          navigationModule,
          storageModule,
          resourcesModule,
          serviceBrokerModule,
          dataStoreModule,
          )
      )
    }
    AppCompatDelegate.setDefaultNightMode(
      AppCompatDelegate.MODE_NIGHT_NO
    )
  }

}