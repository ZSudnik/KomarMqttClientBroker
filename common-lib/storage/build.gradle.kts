plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies {
            api(libs.kotlin.stdlib)
            implementation(libs.androidx.core)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation( libs.koin.android)
                api(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "pl.gov.coi.common.storage"
    resourcePrefix = "common_storage"
}
