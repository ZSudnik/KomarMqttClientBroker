pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
    buildscript {
        repositories {
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
//    versionCatalogs {
//        create("vers") {
//            version( "gradle_android", "8.3.1" )
//            version( "coroutines", "1.8.0" )
//            version( "kotlin", "1.9.23" )
//            version( "compose", "1.6.1" )
//            version( "atomicfu", "0.23.2" )
////            from(files("main/kotlin/build_version.gradle.kt"))
//        }
//    }
}

//dependencyResolutionManagement {
//    versionCatalogs { // <1>
//        create("libs", { from(files("../gradle/libs.versions.toml")) })
//    }
//}

rootProject.name = "buildSrc"
