plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:lifecycle"))
                implementation( "androidx.appcompat:appcompat:${ver.androidx.appcompat}")
                implementation( "androidx.navigation:navigation-compose:${ver.androidx.navigation_compose}")
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.permission"
    resourcePrefix = "common_permission"
}
