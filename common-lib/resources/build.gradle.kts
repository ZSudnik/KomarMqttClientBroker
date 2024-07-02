plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( libs.koin.android)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.mod.common.resources"
    resourcePrefix = "common_resources"
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
}
