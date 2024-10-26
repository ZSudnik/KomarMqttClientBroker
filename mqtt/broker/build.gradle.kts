plugins {
    kotlin("multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    jvm()
    sourceSets {
        commonMain  {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.coroutines.core)
                implementation(libs.ktor.server.cio)
//                implementation("io.ktor:ktor-server-core:2.3.8")
//                implementation("io.ktor:ktor-server-network:2.3.8")
                //ver my
                implementation (project(":mqtt:codec"))
                //ver org
//                api(libs.netty.codec.mqtt)
//                implementation(libs.netty.codec.http)
//                implementation(libs.netty.transport.native.epoll)
                ////////////////////////////

                implementation(libs.h2.mvstore)
                implementation(libs.commons.codec)
//                implementation("com.zaxxer:HikariCP:2.4.7")
//                implementation("com.librato.metrics:metrics-librato:5.1.0")
//                implementation("com.bugsnag:bugsnag:[3.0,4.0)")

                implementation(libs.slf4j.api)
            }
        }
        commonTest{}
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
