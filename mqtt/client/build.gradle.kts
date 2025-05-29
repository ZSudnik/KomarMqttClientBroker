plugins {
    id("codec-module")
}

dependencies {
    api(project(mapOf("path" to ":mqtt:codec")))
    implementation(libs.coroutines.core)
    api(libs.ktor.server.core)
    api(libs.ktor.events)
    api(libs.ktor.io)
    api(libs.ktor.network)
    api(libs.ktor.utils)

}
