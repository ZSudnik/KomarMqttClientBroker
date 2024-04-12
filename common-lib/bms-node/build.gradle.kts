plugins {
    id("android-library-module")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:ui"))
                implementation(project(":common-lib:bms-model"))
                implementation(compose.material)
                implementation("com.github.ajalt.colormath:colormath:3.4.0")
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
