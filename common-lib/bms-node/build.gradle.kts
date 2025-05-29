plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.kotlin.stdlib)
            api(project(":common-lib:bms-model"))
            api(project(":common-lib:ui"))

        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.material)
                implementation(libs.colormath.ajalt)
                implementation(libs.androidx.animation.core)
                implementation(libs.androidx.foundation.layout)
                implementation(libs.androidx.foundation)
                implementation(libs.androidx.material)
                api(libs.androidx.runtime)
                implementation(libs.androidx.ui.geometry)
                implementation(libs.androidx.ui.graphics)
                implementation(libs.androidx.ui.unit)
                api(libs.androidx.ui)
                implementation(libs.coroutines.core)
            }
        }
    }
}

android {
    namespace = "com.zibi.mod.common.bms"
    resourcePrefix = "common_bms_node"
}
