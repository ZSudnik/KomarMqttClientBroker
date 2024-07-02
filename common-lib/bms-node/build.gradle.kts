plugins {
    id("android-library-module")
//    id("org.jetbrains.compose")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:ui"))
                implementation(project(":common-lib:bms-model"))
                implementation(libs.compose.material)
                implementation(libs.colormath)
            }
        }
    }
}

android {
    namespace = "com.zibi.mod.common.bms"
    resourcePrefix = "common_bms_node"
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = ver.build.compose_compiler
//    }
}
