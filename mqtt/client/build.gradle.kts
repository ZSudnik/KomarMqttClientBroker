plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mqtt:codec"))

                implementation(libs.coroutines.core)
                implementation(libs.atomicfu)

                api(libs.ktor.server.cio)
                api(libs.ktor.server.core)

//                implementation("io.ktor:ktor-network-tls:${ver.various.ktor}")
                implementation(kotlin("stdlib"))
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
