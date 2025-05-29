plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:error"))
                implementation( libs.flowredux.jvm)
                implementation( libs.flowredux.compose)
                implementation( libs.koin.androidx.compose)
                implementation(libs.coroutines.android)
            }
        }
    }
}

android {
    namespace = "com.zibi.broker.fragment.start"
    resourcePrefix = "fragment_start"
}
dependencies {
    api(libs.androidx.navigation.common)
    api(libs.flowredux.main)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.runtime)
    api(libs.coroutines.core)
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    api(project(":common-lib:navigation"))
    api(project(":common-lib:resources"))
    api(project(":service-broker"))
    implementation(project(":common-lib:ui"))
    implementation(project(":data-store:broker"))
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core.viewmodel)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.koin.androidx.compose)
}
