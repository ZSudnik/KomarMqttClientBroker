import java.io.FileInputStream
import java.util.Properties

plugins {
    kotlin("android")
    id("com.android.application")
//    id("com.google.devtools.ksp")
}

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = ver.build.java_compatibility.toString()
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }

android {
    namespace = "com.zibi.app.ex.client"
//    resourcePrefix = "app_client"
    compileSdk = ver.build.compile_sdk
    defaultConfig {
        applicationId ="com.zibi.client"
        minSdk = ver.build.min_sdk
        versionName = "1.0.2"
        versionCode = 3
//        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    signingConfigs {
//        create("release") {
//            val keystoreProperties =  Properties()
//            val keystorePropertiesFile = rootProject.file("release_keystore.keystore.jks")
//            if (keystorePropertiesFile.exists()) {
//                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
//                storeFile = keystorePropertiesFile
//                keyAlias = keystoreProperties.getProperty("keyAlias")
//                keyPassword =keystoreProperties.getProperty("keyPassword")
//                storePassword =keystoreProperties.getProperty("storePassword")
////            enableV1Signing = true
////            enableV2Signing = true
//            }
//        }
    }
    buildTypes {
        getByName("release") {
//            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = ver.build.java_compatibility
        targetCompatibility = ver.build.java_compatibility
    }
    buildFeatures {
//        viewBinding = true
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ver.build.compose_compiler
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes.add("META-INF/*")
        }
    }
}

dependencies {
    implementation( project(":common-lib:navigation"))
    implementation( project(":common-lib:error"))
    implementation( project(":common-lib:resources"))
    implementation( project(":common-lib:lifecycle"))
    implementation( project(":common-lib:storage"))
    implementation( project(":fragment:client-start"))
    implementation( project(":fragment:client-settings"))
    implementation( project(":fragment:permission"))
    implementation( project(":data-store:client"))
    implementation( project(":service-client"))

    implementation( "androidx.appcompat:appcompat:${ver.androidx.appcompat}")
    implementation( "androidx.activity:activity-compose:${ver.androidx.activity_compose}")

    implementation("io.insert-koin:koin-android:${ver.various.koin}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")

    coreLibraryDesugaring( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
}