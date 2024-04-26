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

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ver.jetbrains.coroutines}")
                implementation("org.jetbrains.kotlinx:atomicfu:${ver.various.atomicfu}")

                api("io.ktor:ktor-server-cio:${ver.various.ktor}")
                api("io.ktor:ktor-server-core:${ver.various.ktor}")

//                implementation("io.ktor:ktor-network-tls:${ver.various.ktor}")
                implementation(kotlin("stdlib"))
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = ver.build.java_compatibility.toString()
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
