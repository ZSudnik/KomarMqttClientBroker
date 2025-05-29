plugins {
    id("codec-module")
}

 dependencies {
     api(project(mapOf("path" to ":mqtt:codec")))
     api(libs.ktor.network)
     implementation(libs.ktor.io)
     implementation(libs.coroutines.core)
     //ver org
//         api(libs.netty.codec.mqtt)
//         implementation(libs.netty.codec.http)
//         implementation(libs.netty.transport.native.epoll)
     ////////////////////////////
     api(libs.h2.mvstore)
     implementation(libs.commons.codec)
//          implementation("com.zaxxer:HikariCP:2.4.7")
//          implementation("com.librato.metrics:metrics-librato:5.1.0")
//          implementation("com.bugsnag:bugsnag:[3.0,4.0)")
     implementation(libs.slf4j.api)
 }