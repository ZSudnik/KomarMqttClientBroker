plugins {
    kotlin("multiplatform")
    id("android-library-module")
}

kotlin {
    androidTarget()
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":data-store:broker"))
                implementation( project(":common-lib:resources"))
                //ver moja
                implementation( project(":mqtt:broker"))
                implementation(libs.slf4j.simple)
//ver running
//    implementation ("io.moquette:moquette-broker:0.15")

                implementation(libs.koin.android)
//                implementation("io.insert-koin:koin-androidx-navigation:${ver.various.koin}")
//                implementation("org.jetbrains.kotlin:kotlin-stdlib:${ver.jetbrains.kotlin}")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ver.jetbrains.coroutines}")
                implementation(libs.coroutines.android)
                implementation(libs.coroutines.core)
            }
            commonTest {
                dependencies{
                    implementation (libs.junit)
                    implementation(libs.androidx.test.monitor)
                }
            }
        }
        task("testClasses").doLast {
            println("This is a dummy testClasses task")
        }
    }
}
android {
    namespace= "com.zibi.service.broker"
}
dependencies {
    implementation(project(":mqtt:broker"))
}
