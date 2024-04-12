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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${ver.jetbrains.coroutines}")
                implementation("org.jetbrains.kotlinx:atomicfu:${ver.various.atomicfu}")
                implementation( kotlin("stdlib"))
//                implementation( kotlin("stdlib-common"))
                implementation("io.ktor:ktor-server-cio:${ver.various.ktor}")
//                implementation("io.ktor:ktor-io:2.3.8")
//                implementation("io.ktor:ktor-utils:2.3.8")
//                implementation("io.ktor:ktor-network:2.3.8")
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
