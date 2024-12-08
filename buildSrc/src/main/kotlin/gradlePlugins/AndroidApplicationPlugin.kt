package gradlePlugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationPlugin : Plugin<Project> {

    private val Project.android: BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
            ?: error("Not an Android module: $name")

    override fun apply(project: Project) =
        with(project) {
            val libs = project.rootProject
                .extensions
                .getByType(VersionCatalogsExtension::class.java)
                .named("libs")
            applyPlugins()
            androidConfig(libs)
            dependenciesConfig( libs)
        }

    private fun Project.applyPlugins() {
        plugins.run {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
    }

    private fun Project.androidConfig(libs: VersionCatalog) {
        val javaVer = JavaVersion.valueOf(libs.findVersion("java_compatibility").get().displayName)
        android.run {
            compileSdkVersion( libs.findVersion("compile_sdk").get().displayName.toInt())
            defaultConfig {
                minSdk = libs.findVersion("min_sdk").get().displayName.toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                targetSdk = libs.findVersion("compile_sdk").get().displayName.toInt()
//                versionCode = libs.findVersion("versionCode").get().displayName.toInt()
//                versionName = libs.findVersion("versionName").get().displayName
                multiDexEnabled = true
            }
            buildTypes {
                getByName("debug") {
                    isDebuggable = true
                    isMinifyEnabled = false
                    isShrinkResources = false
                }
                getByName("release") {
                    isDebuggable = false
                    isJniDebuggable = false
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
                    signingConfig = signingConfigs.getByName("debug")
                }
            }
            compileOptions {
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = javaVer
                targetCompatibility = javaVer
            }
            lintOptions {
                isCheckReleaseBuilds = false
//                isExplainIssues = true
//                isAbortOnError = false
//                isAbsolutePaths = true
//                disable.add("MissingTranslation")
//                baseline = file("lint-baseline.xml")
            }
//            tasks.withType<KotlinCompile>().configureEach {
//                kotlinOptions.jvmTarget = javaVer.toString()
//            }
            tasks.withType<KotlinCompile>().configureEach {
                    compilerOptions.languageVersion
                        .set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                }
//            composeOptions {
//                kotlinCompilerExtensionVersion = libs.findVersion("compose_compiler").get().displayName
//            }
//            packaging {
//                resources {
//                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
//                    excludes.add("META-INF/*")
//                }
//            }
//            buildFeatures {
////        viewBinding = true
//                compose = true
//                buildConfig = true
//            }
        }
    }
    private fun Project.dependenciesConfig(libs: VersionCatalog) {
        val verDesugar = libs.findVersion("android-desugaring").get().displayName
        dependencies {
            "coreLibraryDesugaring"( "com.android.tools:desugar_jdk_libs:${verDesugar}")
        }
    }
}