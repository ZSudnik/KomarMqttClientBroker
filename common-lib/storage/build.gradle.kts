plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation("androidx.core:core:${ver.androidx.core}")
                implementation( "io.insert-koin:koin-android:${ver.various.koin}")

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
