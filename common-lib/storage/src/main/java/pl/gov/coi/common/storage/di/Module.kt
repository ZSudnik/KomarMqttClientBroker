package pl.gov.coi.common.storage.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pl.gov.coi.common.storage.assets.AssetsProperties
import pl.gov.coi.common.storage.assets.AssetsPropertiesImpl
import pl.gov.coi.common.storage.file.*


val storageModule = module{
  factoryOf( ::AssetsPropertiesImpl){ bind<AssetsProperties>() } //  factoryOf<AssetsProperties>(::AssetsPropertiesImpl)
  factoryOf( ::FileFactoryImpl){ bind<FileFactory>() } //   factoryOf<FileFactory>( ::FileFactoryImpl)
  factoryOf( ::FileConverterImpl){ bind<FileConverter>() } //   factoryOf<FileConverter>( ::FileConverterImpl)
  factoryOf( ::EnvFileProviderImpl){ bind<EnvFileProvider>() } //   factoryOf<EnvFileProvider>( ::EnvFileProviderImpl)
}
