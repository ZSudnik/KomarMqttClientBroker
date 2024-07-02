plugins {
    id("android-library-module")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:navigation"))
                implementation( project(":common-lib:ui"))
                implementation( project(":common-lib:error"))
                implementation( project(":common-lib:resources"))

                implementation( libs.flowredux.jvm)
                implementation( libs.flowredux.compose)

                implementation( libs.koin.androidx.compose)

                implementation(libs.kotlinx.coroutines.android)
                implementation( libs.kotlin.reflect)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }

}

android {
    namespace = "com.zibi.fragment.permission"
    resourcePrefix = "fragment_permission"
}
dependencies {
    implementation(kotlin("reflect"))
}