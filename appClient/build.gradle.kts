plugins {
    id("android-application-module")
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
    implementation( project(":common-lib:resources"))
    implementation( project(":common-lib:storage"))
    implementation( project(":fragment:client-start"))
    implementation( project(":fragment:client-settings"))
    implementation( project(":fragment:permission"))
    implementation( project(":data-store:client"))
    implementation( project(":service-client"))

    implementation( libs.androidx.core)

    implementation(libs.koin.android)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)

    implementation(libs.coroutines.core)
  implementation(libs.androidx.activity)
  implementation(libs.annotation)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.runtime)
  implementation(libs.androidx.fragment)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.navigation.runtime)
  implementation(libs.koin.core)
  implementation("io.ktor:ktor-server-host-common:2.3.11")
}