plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( "com.google.code.gson:gson:${ver.google.gson}")
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.error"
    resourcePrefix = "common_error"
}
