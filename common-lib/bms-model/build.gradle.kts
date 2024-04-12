plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:ui"))
                implementation( "com.google.code.gson:gson:${ver.google.gson}")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
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

java{
    sourceCompatibility = ver.build.java_compatibility
    targetCompatibility = ver.build.java_compatibility
}
//android {
//    namespace = "com.zibi.common.device"
//    resourcePrefix = "common_bms_model"
//    compileSdk = ver.build.compile_sdk
//    defaultConfig {
//        minSdk = ver.build.min_sdk
//    }
//    buildTypes {
//        release {
//            isMinifyEnabled =  true
//        }
//    }
//    compileOptions {
//        sourceCompatibility = ver.build.java_compatibility
//        targetCompatibility = ver.build.java_compatibility
//    }
//}
