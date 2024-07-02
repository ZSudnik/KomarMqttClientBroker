plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
//                api( "io.netty:netty-codec:4.1.106.Final")
                implementation(libs.coroutines.core)
                implementation(libs.atomicfu)
                implementation( kotlin("stdlib"))
//                implementation( kotlin("stdlib-common"))
                implementation(libs.ktor.server.cio)
//                implementation("io.ktor:ktor-io:2.3.8")
//                implementation("io.ktor:ktor-utils:2.3.8")
//                implementation("io.ktor:ktor-network:2.3.8")
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
