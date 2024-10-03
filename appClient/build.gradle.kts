plugins {
    id("android-application-module")
    alias(libs.plugins.compose.compiler)
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}

android {
    namespace = "com.zibi.app.ex.client"
    defaultConfig {
        applicationId ="com.zibi.client"
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

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
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

    implementation( libs.androidx.core)
    implementation( libs.compose.activity)

    implementation(libs.koin.android)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)

    implementation(libs.coroutines.core)
}