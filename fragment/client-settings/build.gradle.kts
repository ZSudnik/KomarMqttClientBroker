plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.kotlin.stdlib)
            api(project(":common-lib:navigation"))
            api(project(":common-lib:resources"))
            api(project(":data-store:client"))
            implementation(project(":common-lib:ui"))
            implementation(libs.compose.activity)
            implementation(libs.koin.androidx.compose)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:ui"))
                implementation(project(":common-lib:error"))

                implementation(libs.flowredux.jvm)
                implementation(libs.flowredux.compose)

                implementation(libs.coroutines.android)
                implementation(libs.compose.material)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.animation)
                implementation(libs.androidx.foundation.layout)
                implementation(libs.androidx.foundation)
                implementation(libs.androidx.material)
                api(libs.androidx.runtime)
                implementation(libs.androidx.ui.graphics)
                implementation(libs.androidx.ui.text)
                implementation(libs.androidx.ui.tooling.preview)
                implementation(libs.androidx.ui.unit)
                implementation(libs.androidx.ui)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                api(libs.androidx.lifecycle.viewmodel)
                api(libs.androidx.navigation.common)
                implementation(libs.androidx.navigation.runtime)
                api(libs.flowredux.main)
                api(libs.state.machine)
                implementation(libs.koin.androidx.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.core.viewmodel)
                api(libs.koin.core)
                api(libs.kotlin.stdlib)
                api(libs.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.zibi.client.fragment.setting"
    resourcePrefix = "fragment_setting"
}
