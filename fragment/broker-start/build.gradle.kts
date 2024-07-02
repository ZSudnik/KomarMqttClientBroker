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
                implementation( project(":data-store:broker"))
                implementation( project(":service-broker"))

                implementation( libs.flowredux.jvm)
                implementation( libs.flowredux.compose)
                implementation( libs.koin.androidx.compose)
                implementation(libs.kotlinx.coroutines.android)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.broker.fragment.start"
    resourcePrefix = "fragment_start"
}

dependencies {
    testImplementation( libs.junit)
}