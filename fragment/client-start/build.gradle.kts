plugins {
    id("android-library-module")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget("android")
 //   jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":common-lib:navigation"))
                implementation( project(":common-lib:ui"))
                implementation( project(":common-lib:error"))
                implementation( project(":common-lib:resources"))
                implementation(project(":common-lib:bms-node"))
                implementation(project(":common-lib:bms-model"))
                implementation( project(":data-store:client"))
                implementation( project(":service-client"))

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
    namespace = "com.zibi.client.fragment.start"
    resourcePrefix = "fragment_start"
//    buildFeatures {
//        compose = true
//    }
}
dependencies {
    implementation(kotlin("reflect"))
}