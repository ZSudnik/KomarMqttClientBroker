plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.core)
                implementation( libs.koin.android)

            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "pl.gov.coi.common.storage"
    resourcePrefix = "common_storage"
}
