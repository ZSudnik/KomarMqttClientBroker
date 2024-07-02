plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
//                implementation( "androidx.appcompat:appcompat:${ver.androidx.appcompat}")
                implementation( libs.androidx.core)
                implementation( libs.androidx.navigation.compose)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.livecycle"
    resourcePrefix = "common_navigation"
}
