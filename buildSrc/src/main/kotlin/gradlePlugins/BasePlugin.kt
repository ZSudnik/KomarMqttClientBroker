package gradlePlugins

import gradlePlugin.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BasePlugin(): Plugin<Project> {

    open fun applyPlugins( project: Project) {
        project.plugins.run {
            apply(libs.pluginsId.github.versions)
            apply(libs.pluginsId.dependency.analysis)
        }
    }

    open fun dependenciesConfig( project: Project) {
        project.dependencies.apply {
            add("coreLibraryDesugaring", libs.android.desugar)
        }
    }

//    open fun androidConfig(libs: VersionCatalog, project: Project) {
//        val javaVer = JavaVersion.valueOf(libs.findVersion("java_compatibility").get().displayName)
//        project.android<BaseAppModuleExtension> {
//            compileSdk = libs.findVersion("compile_sdk").get().displayName.toInt()
//            defaultConfig {
//                minSdk = libs.findVersion("min_sdk").get().displayName.toInt()
//                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//                targetSdk = libs.findVersion("compile_sdk").get().displayName.toInt()
//                multiDexEnabled = true
//            }
//            buildTypes {
//                getByName("debug") {
//                    isDebuggable = true
//                    isMinifyEnabled = false
//                    isShrinkResources = false
//                }
//                getByName("release") {
//                    isDebuggable = false
//                    isJniDebuggable = false
//                    isMinifyEnabled = false
//                    proguardFiles(
//                        getDefaultProguardFile("proguard-android.txt"),
//                        "proguard-rules.pro"
//                    )
//                    signingConfig = signingConfigs.getByName("debug")
//                }
//            }
//            compileOptions {
//                isCoreLibraryDesugaringEnabled = true
//                sourceCompatibility = javaVer
//                targetCompatibility = javaVer
//            }
//            lint {
//                checkReleaseBuilds = false
//            }
//////            tasks.withType<KotlinCompile>().configureEach {
//////                kotlinOptions.jvmTarget = javaVer.toString()
//////            }
//            project.tasks.withType<KotlinCompile>().configureEach {
//                compilerOptions.languageVersion
//                    .set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
//            }
//////            composeOptions {
//////                kotlinCompilerExtensionVersion = libs.findVersion("compose_compiler").get().displayName
//////            }
//////            packaging {
//////                resources {
//////                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//////                    excludes.add("META-INF/*")
//////                    excludes.add("META-INF/*kotlin_module")
//////                }
//////            }
//////            testOptions {
//////                unitTests.isIncludeAndroidResources = true
//////            }
//////            buildFeatures {
//////                aidl = true
//////                viewBinding = true
//////                dataBinding = true
//////                buildConfig = true
//////            }
//        }
//    }


////    private fun BaseExtension.lint(action: Lint.() -> Unit) {
////        (this as CommonExtension<*, *, *, *, *, *>).lint(action)
////    }

}