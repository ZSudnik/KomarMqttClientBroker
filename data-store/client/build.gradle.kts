plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:resources"))
                implementation( project(":common-lib:bms-model"))
                api (libs.androidx.datastore.preferences)

                implementation(libs.kotlinx.coroutines.android)
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
dependencies {
    implementation(project(":common-lib:resources"))
}
