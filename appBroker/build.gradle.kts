plugins {
    id("android-application-module")
    alias(libs.plugins.compose.compiler)
}
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }

android {
    namespace = "com.zibi.app.ex.broker"
    defaultConfig {
        applicationId ="com.komar.broker"
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
    implementation( project(":fragment:broker-start"))
    implementation( project(":fragment:broker-settings"))
    implementation( project(":data-store:broker"))
    implementation( project(":service-broker"))

    implementation( libs.androidx.appcompat)
    implementation( libs.compose.activity)

    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.android)

}