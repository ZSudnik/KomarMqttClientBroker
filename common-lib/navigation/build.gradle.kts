plugins {
    alias(libs.plugins.compose.compiler)
    id("android-library-module")
}

kotlin {
    androidTarget("android")
}

android {
    namespace = "com.zibi.mod.common.navigation"
    resourcePrefix = "common_navigation"
}

dependencies {
    implementation( project(":common-lib:ui"))
    implementation( project(":common-lib:error"))
    implementation( project(":common-lib:lifecycle"))

    api( libs.androidx.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.koin.androidx.compose)
}