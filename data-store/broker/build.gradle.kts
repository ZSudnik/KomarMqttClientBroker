plugins {
    kotlin("multiplatform")
    id("android-library-module")
}

kotlin {
    androidTarget()
    sourceSets {
        commonMain {
            dependencies {
                api (libs.androidx.datastore.preferences)
                implementation(libs.coroutines.android)
                implementation(libs.koin.android)
            }
            commonTest {
                dependencies{
                        implementation (libs.junit)
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