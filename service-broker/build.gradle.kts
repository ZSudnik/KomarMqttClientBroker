plugins {
    kotlin("multiplatform")
    id("com.android.library")
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
                implementation("org.slf4j:slf4j-simple:${ver.various.slf4j}")
//ver running
//    implementation ("io.moquette:moquette-broker:0.15")

                implementation("io.insert-koin:koin-android:${ver.various.koin}")
//                implementation("io.insert-koin:koin-androidx-navigation:${ver.various.koin}")

//                implementation("org.jetbrains.kotlin:kotlin-stdlib:${ver.jetbrains.kotlin}")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ver.jetbrains.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
            }
            commonTest {
                dependencies{
                    implementation ("junit:junit:${ver.various.junit}")
                    implementation("androidx.test:monitor:1.6.1")
                }
            }
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions.jvmTarget = ver.build.java_compatibility.toString()
        }
        task("testClasses").doLast {
            println("This is a dummy testClasses task")
        }
    }
}

android {
    compileSdk= ver.build.compile_sdk
    buildToolsVersion = ver.build.build_tools

    namespace= "com.zibi.service.broker"

    defaultConfig {
        minSdk = ver.build.min_sdk
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled= true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = ver.build.java_compatibility
        targetCompatibility = ver.build.java_compatibility
    }
//    buildFeatures {
//        aidl true
//    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//            excludes.add("META-INF/*")
//        }
//    }

}

dependencies {
    implementation("androidx.test:monitor:1.6.1")
    coreLibraryDesugaring( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
}