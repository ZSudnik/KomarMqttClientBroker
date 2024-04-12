plugins {
    id("android-library-module")
    id("org.jetbrains.compose")
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

                implementation( "com.freeletics.flowredux:flowredux-jvm:${ver.various.flow_redux}")
                implementation( "com.freeletics.flowredux:compose:${ver.various.flow_redux}")

                implementation( "io.insert-koin:koin-androidx-compose:${ver.various.koin}")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
                implementation(compose.material)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}

android {
    namespace = "com.zibi.broker.fragment.setting"
    resourcePrefix = "fragment_setting"
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = ver.build.compose_compiler
//    }
}

dependencies {
    testImplementation("junit:junit:${ver.various.junit}")
}

