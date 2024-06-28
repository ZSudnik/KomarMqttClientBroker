val gradle_android = "8.5.0"
//val coroutines = "1.8.0" // two place change
val kotlin = "1.9.23" // two place change
val compose = "1.6.1"
val atomicfu = "0.23.2" // two place change

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlin}")
    implementation( "org.jetbrains.kotlin:kotlin-reflect:${kotlin}")

    implementation("com.android.tools.build:gradle:${gradle_android}")
    implementation("com.android.tools.build:gradle-api:${gradle_android}")

    implementation("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${atomicfu}")
    implementation("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:${compose}")
//    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detekt")
//    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.23-1.0.19")
}

gradlePlugin {
    plugins {
        create("android-library-module") {
            id = "android-library-module"
            implementationClass = "gradlePlugins.AndroidLibraryPlugin"
        }
    }
}
