package com.zibi.mod.common.resources.di

import com.zibi.mod.common.resources.StringResolver
import com.zibi.mod.common.resources.StringResolverImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val resourcesModule = module{
    factory<StringResolver>{ StringResolverImpl(resources = this.androidContext().resources) }
}
