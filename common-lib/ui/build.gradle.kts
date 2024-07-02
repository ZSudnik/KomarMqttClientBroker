plugins {
    id("android-library-module")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget("android")
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                api(libs.compose.material)
                api(libs.compose.material3)
                implementation(libs.compose.foundation)
                implementation(libs.compose.uiTooling)
                implementation(libs.compose.ui)
                api(libs.compose.preview)
                implementation(libs.compose.animation)
                implementation(libs.compose.animationGraphics)
                implementation(libs.compose.materialIconsExtended)

                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.constraintlayout.compose)
                implementation(libs.androidx.appcompat)

                implementation( libs.androidx.customview.poolingcontainer)

                implementation( libs.androidx.camera.camera2)
                implementation( libs.androidx.camera.lifecycle)
                implementation( libs.androidx.camera.view)
                implementation( libs.zxing.core)

                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.androidx.media3.ui)

                implementation( libs.lottie.compose)
                implementation (libs.androidx.lifecycle.viewmodel)
                implementation (libs.androidx.lifecycle.common)

                implementation(libs.colormath)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.ui"
    resourcePrefix = "common_ui"
//    buildFeatures {
//        compose = true
//    }
}