plugins {
    kotlin("multiplatform")
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.kotlin.stdlib)
            implementation(libs.koin.android)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.androidx.datastore.preferences)
                implementation(libs.coroutines.android)
                implementation(libs.koin.android)

                api(libs.androidx.datastore.core)
                api(libs.androidx.datastore.preferences.core)
                api(libs.koin.core)
                implementation(libs.androidx.datastore)
                implementation(libs.coroutines.core)
            }
            commonTest {
                dependencies {
                    implementation(libs.junit)
                }
            }
        }
    }
}

android {
    namespace = "com.zibi.mod.data_store"
}