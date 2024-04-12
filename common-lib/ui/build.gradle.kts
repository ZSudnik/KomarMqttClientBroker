plugins {
    id("org.jetbrains.compose")
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                api(compose.material)
                api(compose.material3)
                implementation(compose.foundation)
                implementation(compose.uiTooling)
                implementation(compose.ui)
                api(compose.preview)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.materialIconsExtended)

                implementation("androidx.navigation:navigation-compose:${ver.androidx.navigation_compose}")
                implementation("androidx.constraintlayout:constraintlayout-compose:${ver.androidx.constraintlayout_compose}")
                implementation("androidx.appcompat:appcompat:${ver.androidx.appcompat}")

                implementation( "androidx.customview:customview-poolingcontainer:${ver.androidx.customview_poolingcontainer}")

                implementation( "androidx.camera:camera-camera2:${ver.androidx.camerax}")
                implementation( "androidx.camera:camera-lifecycle:${ver.androidx.camerax}")
                implementation( "androidx.camera:camera-view:${ver.androidx.camerax}")
                implementation( "com.google.zxing:core:${ver.google.zxing_core}")

                implementation("androidx.media3:media3-exoplayer:${ver.google.exoplayer}")
                implementation("androidx.media3:media3-ui:${ver.google.exoplayer}")

                implementation( "com.airbnb.android:lottie-compose:${ver.various.lottie}")
                implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:${ver.androidx.lifecycle}")
                implementation ("androidx.lifecycle:lifecycle-common:${ver.androidx.lifecycle}")

                implementation("com.github.ajalt.colormath:colormath:${ver.various.colormath}")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = ver.build.java_compatibility.toString()
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.ui"
    resourcePrefix = "common_ui"
    compileSdk = ver.build.compile_sdk
    defaultConfig {
        minSdk = ver.build.min_sdk
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = ver.build.java_compatibility
        targetCompatibility = ver.build.java_compatibility
    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = ver.build.compose_compiler
//    }
}

dependencies {
    coreLibraryDesugaring( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
}