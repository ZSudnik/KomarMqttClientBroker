plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    sourceSets {
        commonMain {
            dependencies {
                api ("androidx.datastore:datastore-preferences:${ver.androidx.datastore_preferences}")
//                implementation("org.jetbrains.kotlin:kotlin-stdlib:${ver.jetbrains.kotlin}")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ver.jetbrains.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${ver.jetbrains.coroutines}")
                implementation("io.insert-koin:koin-android:${ver.various.koin}")
            }
            commonTest {
                dependencies{
                        implementation ("junit:junit:${ver.various.junit}")
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

    namespace= "com.zibi.mod.data_store"

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
}

dependencies {
    coreLibraryDesugaring( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
}