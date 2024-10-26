import org.gradle.configurationcache.extensions.capitalized

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
    buildscript {
        repositories {
            google()
            mavenCentral()
//            maven {
//                url = uri("https://storage.googleapis.com/r8-releases/raw")
//            }
        }
//        dependencies {
//            classpath("com.android.tools:r8:8.2.42")
//        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":buildSrc:testClasses"))
//val typApp = "client"
val typApp = "broker"
rootProject.name = "${typApp.replaceFirstChar(Char::uppercase)} Mqtt - Komar"
include (":app${typApp.replaceFirstChar(Char::uppercase)}")
include(":fragment:$typApp-start")
include(":fragment:$typApp-settings")
include(":fragment:permission")
include(":data-store:$typApp")
include(":service-$typApp")
if(typApp == "client") {
    include(":common-lib:bms-node")
    include(":common-lib:bms-model")
}
include(":common-lib:ui")
include(":common-lib:resources")
include(":common-lib:navigation")
include(":common-lib:ui")
include(":common-lib:lifecycle")
include(":common-lib:error")
include(":common-lib:storage")
include(":common-lib:permission")

include(":mqtt:codec")
//include(":mqtt:broker")
//include(":mqtt:client")
include(":mqtt:$typApp")