plugins {
    id("android-library-module")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":data-store:client"))
                implementation( project(":common-lib:resources"))
                api(compose.runtime)

                //ver moja
                implementation( project(":mqtt:client"))

                implementation("io.insert-koin:koin-android:${ver.various.koin}")
                implementation("org.slf4j:slf4j-simple:${ver.various.slf4j}")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
            }
//            commonTest {
//                dependencies{
//                    implementation ("junit:junit:${ver.various.junit}")
//                    implementation("androidx.test:monitor:1.6.1")
//                }
//            }
        }

        task("testClasses").doLast {
            println("This is a dummy testClasses task")
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
    implementation("androidx.test:monitor:1.6.1")
}