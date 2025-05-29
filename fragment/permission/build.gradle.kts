plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:navigation"))
                implementation( project(":common-lib:ui"))
                implementation( project(":common-lib:error"))
                implementation( project(":common-lib:resources"))

                implementation( libs.flowredux.jvm)
                implementation( libs.flowredux.compose)

                implementation( libs.koin.androidx.compose)

                implementation(libs.coroutines.android)
                runtimeOnly( libs.kotlin.reflect)
            }
        }
    }
}

android {
    namespace = "com.zibi.fragment.permission"
    resourcePrefix = "fragment_permission"
}
dependencies {
    runtimeOnly(libs.kotlin.reflect)
    api(project(":common-lib:navigation"))
    api(project(":common-lib:resources"))
    implementation(project(":common-lib:ui"))
    implementation(libs.androidx.navigation.compose.android)
    api(libs.androidx.foundation.layout)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.navigation.common)
    api(libs.androidx.runtime)
    api(libs.coroutines.core)
    api(libs.flowredux.main)
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.core)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.compose.activity)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core.viewmodel)
}