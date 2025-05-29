plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.androidx.camera.core)
            api(libs.androidx.material3)
            api(libs.androidx.runtime.saveable)
            api(libs.androidx.ui.geometry)
            api(libs.androidx.foundation)
            api(libs.androidx.foundation.layout)
            api(libs.androidx.lifecycle.common)
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.androidx.material)
            api(libs.androidx.runtime)
            api(libs.androidx.ui)
            api(libs.androidx.ui.graphics)
            api(libs.androidx.ui.text)
            api(libs.androidx.ui.tooling.preview)
            api(libs.androidx.ui.unit)
            api(libs.coroutines.core)
            api(libs.kotlin.stdlib)
            implementation(libs.androidx.annotation.experimental)
            implementation(libs.androidx.appcompat.resources)
            implementation(libs.androidx.animation.core)
            implementation(libs.androidx.animation.graphics)
            implementation(libs.androidx.ui.util)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.media3.common)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.lottie.main)
            implementation(libs.guava)
            implementation(libs.androidx.animation)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            runtimeOnly(libs.androidx.camera.camera2)
            implementation(libs.androidx.constraintlayout.core)
            implementation(libs.androidx.core)
            implementation(libs.annotation)
            implementation(libs.lottie.compose)
            implementation(libs.zxing.core)
            implementation(libs.androidx.lifecycle.runtime.compose.android)
            implementation(libs.androidx.constraintlayout.compose.android)
        }
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.compose.material)
                api(libs.compose.preview)
                implementation(libs.compose.foundation)
                implementation(libs.compose.uiTooling)
                implementation(libs.compose.ui)
                implementation(libs.compose.animation)
                implementation(libs.compose.animationGraphics)
                implementation(libs.compose.materialIconsExtended)
                implementation(libs.zxing.core)
                implementation(libs.colormath.ajalt)
            }
        }
        val androidMain by getting
        val jvmMain by getting
    }
}

android {
    namespace = "com.zibi.mod.common.ui"
    resourcePrefix = "common_ui"
}
