package com.zibi.service.client.di

import com.zibi.service.client.log.LogStream
import com.zibi.service.client.log.LogStreamImp
import com.zibi.service.client.notification.NotificationUtil
import com.zibi.service.client.service.MQTTService
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val serviceClientModule = module{
    singleOf<LogStream>(::LogStreamImp)
//    factory { NotificationUtil(get(), get()) }
}
