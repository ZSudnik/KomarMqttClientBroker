package com.zibi.mod.data_store.di

import com.zibi.mod.data_store.preferences.ClientSetting
import com.zibi.mod.data_store.preferences.LightBulbStore
import com.zibi.mod.data_store.preferences.UserLogin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val dataStoreModule = module{
    singleOf(::ClientSetting)
    singleOf(::UserLogin)
    singleOf(::LightBulbStore)
//    single<DataStore<Preferences>> {
//        PreferenceDataStoreFactory.create(
//            corruptionHandler = ReplaceFileCorruptionHandler(
//                produceNewData = { emptyPreferences() }
//            ),
//            migrations = listOf(SharedPreferencesMigration(this.androidContext(), USER_PREFERENCES)),
//            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
//            produceFile = { this.androidContext() .preferencesDataStoreFile(USER_PREFERENCES) }
//        )
//    }
}

//private const val USER_PREFERENCES = "user_setting"