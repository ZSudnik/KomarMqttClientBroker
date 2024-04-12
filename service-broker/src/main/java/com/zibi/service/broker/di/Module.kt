package com.zibi.service.broker.di

import com.zibi.service.broker.log.LogStream
import com.zibi.service.broker.log.LogStreamImp
import com.zibi.service.broker.notification.NotificationUtil
import com.zibi.service.broker.service.MQTTService
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val serviceBrokerModule = module{
    factoryOf(::MQTTService)
    singleOf<LogStream>(::LogStreamImp)
    single { NotificationUtil(get(), get()) }
}
