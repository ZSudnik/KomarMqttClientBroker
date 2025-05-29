plugins {
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
    api( project(":common-lib:lifecycle"))
    api(libs.androidx.activity)
    api(libs.androidx.animation)
    api(libs.androidx.fragment)
    api(libs.androidx.lifecycle.common)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.navigation.common)
    api(libs.androidx.navigation.runtime)
    api(libs.androidx.runtime)
    api(libs.androidx.ui)
    api(libs.coroutines.core)
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.annotation)
    implementation(libs.koin.compose)
    implementation(libs.koin.core.viewmodel)
    implementation( libs.androidx.navigation.compose)
    runtimeOnly(libs.coroutines.android)
    implementation(libs.koin.androidx.compose)
}