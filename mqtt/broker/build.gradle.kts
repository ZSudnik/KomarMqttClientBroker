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
                //    api("io.netty:netty-codec-mqtt:${ver.various.io_netty}")
                //    implementation("io.netty:netty-codec-http:${ver.various.io_netty}")
                //    implementation("io.netty:netty-transport-native-epoll:${ver.various.io_netty}")
                ////////////////////////////
//                api("io.netty:netty-codec-mqtt:${ver.various.io_netty}")
//                implementation("io.netty:netty-codec-http:${ver.various.io_netty}")
//                implementation("io.netty:netty-transport-native-epoll:${ver.various.io_netty}")

                implementation("com.h2database:h2-mvstore:1.4.199")
//                implementation("com.zaxxer:HikariCP:2.4.7")
//                implementation("com.librato.metrics:metrics-librato:5.1.0")
//                implementation("com.bugsnag:bugsnag:[3.0,4.0)")
                implementation("commons-codec:commons-codec:1.15")
                implementation(libs.slf4j.api)
            }
        }
        commonTest{}
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
