package com.zibi.service.client.di

import com.zibi.service.client.log.LogStream
import com.zibi.service.client.log.LogStreamImp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val serviceClientModule = module{
    singleOf<LogStream>(::LogStreamImp)
//    factory { NotificationUtil(get(), get()) }
}
