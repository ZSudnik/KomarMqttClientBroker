package com.zibi.app.ex.client

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.zibi.app.ex.client.di.appModule
import com.zibi.mod.common.navigation.navigationModule
import com.zibi.mod.common.resources.di.resourcesModule
import com.zibi.mod.data_store.di.dataStoreModule
import com.zibi.client.fragment.setting.di.fragmentSettingModule
import com.zibi.client.fragment.start.di.fragmentStartModule
import com.zibi.fragment.permission.di.fragmentPermissionModule
import com.zibi.service.client.di.serviceClientModule
import com.zibi.service.client.service_ktor.configureMqtt
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.plugin
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
          fragmentPermissionModule,
          navigationModule,
          storageModule,
          resourcesModule,
          serviceClientModule,
          dataStoreModule,
          )
      )
    }
    AppCompatDelegate.setDefaultNightMode(
      AppCompatDelegate.MODE_NIGHT_NO
    )

//    val env = applicationEngineEnvironment {
//      module {
//        configureMqtt()
//      }
//    }

//    env.application.plugin(Mqtt)
//    CoroutineScope(Dispatchers.IO).launch {
//      embeddedServer(CIO, port = 8080, host = "localhost") {
//        configureMqtt()
//      }.start(wait = false)
//    }


  }

}

//fun main() {
//  val env = applicationEngineEnvironment {
//    module {
//      configureMqtt()
//    }
//}