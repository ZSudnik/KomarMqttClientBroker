plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.kotlin.stdlib)
            api(project(":common-lib:ui"))
            implementation(libs.gson)
        }
    }
    jvm()
    sourceSets {
        commonMain {}
        commonTest {}
    }
}
android {
    namespace = "com.zibi.mod.common.device"
    resourcePrefix = "common_bms_device"
}