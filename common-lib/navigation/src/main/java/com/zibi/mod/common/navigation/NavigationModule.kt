package com.zibi.mod.common.navigation

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

//singleOf(::SimpleServiceImpl){ bind<SimpleService>() }
//      factoryOf(::FactoryPresenter)
//      viewModelOf(::SimpleViewModel)
//      scope<MyActivity>(){
//        scopedOf(::Session)
//      }
//      workerOf(::SimpleWorker)

val navigationModule = module{
  singleOf( ::NavigationDataManager)
  singleOf(::NavigationDataManager){bind<NavigationDataRetriever>()}
  singleOf(::NavigationDataManager){bind<NavigationDataStorage>()}
  viewModelOf(::NavigationViewModelImpl)

//  viewModelOf(::SimpleViewModel)
//  viewModel{ ::NavigationDataRetriever}

}

//@Module
//@InstallIn(SingletonComponent::class)
//class NavigationModule {
//
//  @Provides
//  @Singleton
//  internal fun provideNavigationDataManager(): NavigationDataManager =
//    NavigationDataManager()
//}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class NavigationModuleBinds {

//  @Binds
//  @Singleton
//  internal abstract fun bindNavigationDataRetriever(
//    navigationDataManager: NavigationDataManager
//  ): NavigationDataRetriever

//  @Binds
//  @Singleton
//  internal abstract fun bindNavigationDataStorage(
//    navigationDataManager: NavigationDataManager
//  ): NavigationDataStorage

//}

//@Qualifier
//annotation class NavigationData

//@Module
//@InstallIn(ViewModelComponent::class)
//class NavigationDataModule {
//
//  @Provides
//  @NavigationData
//  @ViewModelScoped
//  internal fun provideNavigationData(
//    dataRetriever: NavigationDataRetriever
//  ): Any? =
//    dataRetriever.retrieveLastData()
//}
