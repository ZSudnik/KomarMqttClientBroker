plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                api(project(":common-lib:bms-model"))
                api(project(":common-lib:navigation"))
                api(project(":data-store:client"))
                api(project(":service-client"))
                implementation(project(":common-lib:ui"))
                implementation(project(":common-lib:error"))
                implementation(project(":common-lib:resources"))
                implementation(project(":common-lib:ui"))
                api(libs.androidx.foundation.layout)
                api(libs.androidx.lifecycle.viewmodel)
                api(libs.androidx.navigation.common)
                api(libs.androidx.runtime)
                api(libs.coroutines.core)
                api(libs.flowredux.main)
                api(libs.koin.core)
                api(libs.kotlin.stdlib)

                implementation(libs.flowredux.jvm)
                implementation(libs.flowredux.compose)

                implementation(libs.koin.androidx.compose)

                implementation(libs.coroutines.android)
                implementation(libs.kotlin.reflect)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.animation)
                implementation(libs.androidx.foundation)
                implementation(libs.androidx.material3)
                implementation(libs.androidx.material)
                implementation(libs.androidx.ui.graphics)
                implementation(libs.androidx.ui.text)
                implementation(libs.androidx.ui.tooling.preview)
                implementation(libs.androidx.ui.unit)
                implementation(libs.androidx.ui)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.navigation.runtime)
                implementation(libs.koin.androidx.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.core.viewmodel)
            }
        }
    }
}

android {
    namespace = "com.zibi.client.fragment.start"
    resourcePrefix = "fragment_start"
}
dependencies {
    api(project(":common-lib:bms-model"))
    api(project(":common-lib:navigation"))
    api(project(":data-store:client"))
    api(project(":service-client"))
    implementation(project(":common-lib:ui"))
    implementation(project(":common-lib:bms-node"))
    api(libs.androidx.foundation.layout)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.navigation.common)
    api(libs.androidx.runtime)
    api(libs.coroutines.core)
    api(libs.flowredux.main)
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core.viewmodel)
    implementation(libs.kotlin.reflect)
}