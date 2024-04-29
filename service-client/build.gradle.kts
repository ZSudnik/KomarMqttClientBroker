plugins {
    id("org.jetbrains.compose")
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
                api(compose.runtime)

                implementation("io.insert-koin:koin-android:${ver.various.koin}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")

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
//    buildFeatures {
//        aidl = true
//    }
}

dependencies {
    implementation("androidx.test:monitor:1.6.1")
    implementation(project(":mqtt:client"))
    testImplementation("junit:junit:${ver.various.junit}")
}
