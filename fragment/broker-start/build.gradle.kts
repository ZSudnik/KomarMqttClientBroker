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
                implementation( project(":service-broker"))

                implementation( "com.freeletics.flowredux:flowredux-jvm:${ver.various.flow_redux}")
                implementation( "com.freeletics.flowredux:compose:${ver.various.flow_redux}")

                implementation( "io.insert-koin:koin-androidx-compose:${ver.various.koin}")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
//                implementation( "org.jetbrains.kotlin:kotlin-reflect:${ver.jetbrains.kotlin}")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ver.build.compose_compiler
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes.add("META-INF/*")
        }
    }

}

dependencies {
    testImplementation( "junit:junit:${ver.various.junit}")
}
