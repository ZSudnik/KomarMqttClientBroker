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
                implementation( project(":data-store:broker"))

                implementation( libs.flowredux.jvm)
                implementation( libs.flowredux.compose)

                implementation( libs.koin.androidx.compose)

                implementation(libs.coroutines.android)
                implementation(libs.compose.material)
            }
        }
    }
}

android {
    namespace = "com.zibi.broker.fragment.setting"
}
dependencies {
    implementation(libs.androidx.navigation.compose.android)
    api(libs.state.machine)
    api(libs.androidx.foundation.layout)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.navigation.common)
    api(libs.androidx.runtime)
    api(libs.coroutines.core)
    api(libs.flowredux.main)
    api(libs.koin.core)
    api(libs.kotlin.stdlib)
    api(project(":common-lib:navigation"))
    api(project(":common-lib:resources"))
    api(project(":common-lib:ui"))
    api(project(":data-store:broker"))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.core)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.annotation)
    implementation(libs.compose.activity)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core.viewmodel)
}

