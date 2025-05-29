import org.gradle.configurationcache.extensions.capitalized
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    buildscript {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
//            maven {
//                url = uri("https://storage.googleapis.com/r8-releases/raw")
//            }
        }
        dependencies {
//            classpath("com.android.tools:r8:8.2.42")
            classpath("com.electronwill.night-config:toml:3.8.2")
//            classpath("com.squareup:kotlinpoet:2.2.0")
//            classpath("io.tmio:tuweni-toml:2.4.2")
//            classpath("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.19.0")
//            classpath("io.hotmoka:toml4j:0.7.3")
        }
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
val typApp = "client"
//val typApp = "broker"
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
include(":mqtt:$typApp")

val typApp2 = "broker"
include(":appBroker")
include(":mqtt:$typApp2")
include(":fragment:$typApp2-start")
include(":fragment:$typApp2-settings")
include(":data-store:$typApp2")
include(":service-$typApp2")
