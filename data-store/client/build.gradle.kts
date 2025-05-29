plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:bms-model"))
                api(libs.androidx.datastore.preferences)

                implementation(libs.coroutines.android)
                implementation(libs.koin.android)
                implementation(libs.androidx.datastore.core)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.androidx.datastore)
                api(libs.koin.core)
                api(libs.kotlin.stdlib)
                api(libs.coroutines.core)
            }
            commonTest {
                dependencies {
                    implementation(libs.junit)
                    implementation(libs.androidx.datastore.core)
                    implementation(libs.androidx.datastore.preferences.core)
                    implementation(libs.androidx.datastore)
                    api(libs.koin.core)
                    api(libs.kotlin.stdlib)
                    api(libs.coroutines.core)
                    implementation(project(":common-lib:bms-model"))
                }
            }
        }
        task("testClasses").doLast {
            println("This is a dummy testClasses task")
        }
    }
}

android {
    namespace= "com.zibi.mod.data_store"
}
dependencies {
    implementation(project(":common-lib:bms-model"))
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    api(libs.coroutines.core)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore)
}
