plugins {
    kotlin("multiplatform")
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies {
            api(project(":mqtt:broker"))
            api(project(":common-lib:resources"))
            api(project(":data-store:broker"))
            api(libs.koin.core)
            api(libs.kotlin.stdlib)
            implementation(libs.androidx.core)
            implementation(libs.koin.android)
            testImplementation(libs.junit)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                //ver moja
                implementation(libs.slf4j.simple)
//ver running
//    implementation ("io.moquette:moquette-broker:0.15")
                implementation(libs.koin.android)
                implementation(libs.coroutines.android)
                implementation(libs.coroutines.core)
            }
            commonTest {
                dependencies{
                    implementation(libs.androidx.test.monitor)
                }
            }
        }
    }
}
android {
    namespace= "com.zibi.service.broker"
}