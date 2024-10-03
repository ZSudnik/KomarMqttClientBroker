plugins {
    alias(libs.plugins.compose.compiler)
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":mqtt:client"))
                implementation( project(":data-store:client"))
                implementation( project(":common-lib:resources"))
                api(libs.compose.runtime)
//                api(compose.runtime)

                implementation(libs.koin.android)
                implementation(libs.kotlinx.coroutines.android)

//                implementation("io.ktor:ktor-client-core:${ver.various.ktor}")
//                implementation("io.ktor:ktor-client-content-negotiation:${ver.various.ktor}")
//                implementation("io.ktor:ktor-client-okhttp:${ver.various.ktor}")

//                implementation("io.ktor:ktor-client-android:${ver.various.ktor}")
            }
        }

//        task("testClasses").doLast {
//            println("This is a dummy testClasses task")
//        }
    }
}

android {
    namespace= "com.zibi.service.client"
    buildFeatures {
        aidl = true
    }
}

dependencies {
    implementation(libs.androidx.test.monitor)
    implementation(project(":mqtt:client"))
    testImplementation( libs.junit)
}
