plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android")
    sourceSets {
        commonMain {
            dependencies {
                implementation( project(":mqtt:client"))
                implementation( project(":common-lib:resources"))
                implementation( project(":common-lib:bms-model"))
                api ("androidx.datastore:datastore-preferences:${ver.androidx.datastore_preferences}")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
                implementation("io.insert-koin:koin-android:${ver.various.koin}")
            }
            commonTest {
                dependencies{
                        implementation ("junit:junit:${ver.various.junit}")
                }
            }
        }
        task("testClasses").doLast {
            println("This is a dummy testClasses task")
        }
    }
}

android {
    namespace= "com.zibi.mod.data_store"
}
dependencies {
    implementation(project(":common-lib:resources"))
}
