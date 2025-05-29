plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies {
            api(libs.kotlin.stdlib)
            implementation(libs.annotation)
            implementation(libs.koin.android)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(libs.koin.core)
            }
        }
    }
}
android {
    namespace = "com.zibi.mod.common.resources"
    resourcePrefix = "common_resources"
}