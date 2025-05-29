plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android") {
        dependencies {
            api(libs.kotlin.stdlib)
            implementation(libs.gson)
        }
    }
}
android {
    namespace = "com.zibi.mod.common.error"
    resourcePrefix = "common_error"
}
