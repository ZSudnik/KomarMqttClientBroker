plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common-lib:ui"))
                implementation( libs.gson)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    task("testClasses").doLast {
        println("This is a dummy testClasses task")
    }
}
