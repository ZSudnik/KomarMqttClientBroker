plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mqtt:client"))
                implementation(project(":data-store:client"))
                implementation(project(":common-lib:resources"))
                api(libs.compose.runtime)
                implementation(libs.koin.android)
                implementation(libs.coroutines.android)
                implementation(libs.ktor.server.cio)

//                implementation("io.ktor:ktor-client-core:${ver.various.ktor}")
//                implementation("io.ktor:ktor-client-content-negotiation:${ver.various.ktor}")
//                implementation("io.ktor:ktor-client-okhttp:${ver.various.ktor}")

//                implementation("io.ktor:ktor-client-android:${ver.various.ktor}")
                api(libs.androidx.runtime)
                implementation(libs.androidx.core)
                implementation(libs.koin.android)
                api(libs.koin.core)
                api(libs.ktor.server.core)
                implementation(libs.ktor.server.host.common)
                implementation(libs.ktor.utils)
                api(libs.kotlin.stdlib)
                api(libs.coroutines.core)
                implementation(project(":common-lib:resources"))
                api(project(":data-store:client"))
                api(project(":mqtt:codec"))
            }
        }
    }
}

android {
    namespace= "com.zibi.service.client"
//    buildFeatures {
//        aidl = true
//    }
}

dependencies {
    api(project(":data-store:client"))
    api(project(":mqtt:codec"))
    api(project(":mqtt:client"))
    implementation(project(":common-lib:resources"))
    api(libs.androidx.runtime)
    api(libs.coroutines.core)
    api(libs.koin.core)
    api(libs.ktor.server.core)
    api(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.koin.android)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.utils)
    testImplementation(libs.androidx.test.monitor)
}

