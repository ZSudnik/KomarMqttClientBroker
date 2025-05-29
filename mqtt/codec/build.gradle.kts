plugins{
    id("codec-module")
}
dependencies{
    api(libs.ktor.io)
    runtimeOnly(libs.kotlin.reflect)
}
