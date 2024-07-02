plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.atomicfu)
                implementation(libs.ktor.server.cio)
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
