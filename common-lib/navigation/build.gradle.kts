plugins {
    id("org.jetbrains.compose")
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

    api( "androidx.navigation:navigation-compose:${ver.androidx.navigation_compose}")

    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")

    implementation("io.insert-koin:koin-androidx-compose:${ver.various.koin}")
}