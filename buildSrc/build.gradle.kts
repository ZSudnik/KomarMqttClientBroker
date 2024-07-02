plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    alias(libs.plugins.compose.compiler) apply false
}

gradlePlugin {
    plugins {
        create("android-library-module") {
            id = "android-library-module"
            implementationClass = "gradlePlugins.AndroidLibraryPlugin"
        }
        create("android-application-module"){
            id = "android-application-module"
            implementationClass = "gradlePlugins.AndroidApplicationPlugin"
        }
    }
}

dependencies  {
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.gradle.main)
    implementation(libs.gradle.api)
    implementation(libs.kotlinx.atomicfu)
//    implementation(libs.gradle.compose)
//    implementation(libs.plugins.gradle.ksp)
//    implementation(libs.plugins.gradle.detekt)
}
