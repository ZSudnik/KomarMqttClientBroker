package gradlePlugins

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import gradlePlugin.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationPlugin : BasePlugin() {

    private fun <T : BaseAppModuleExtension> Project.android(action: T.() -> Unit) {
        extensions.configure("android", action)
    }

    override fun apply(project: Project) {
        with(project) {
            applyPlugins( project)
            androidConfig( project)
            dependenciesConfig( project)
        }
    }

    override fun applyPlugins( project: Project) {
        super.applyPlugins( project)
        project.plugins.run {
            apply(libs.pluginsId.android.application)
            apply(libs.pluginsId.kotlin.android)
            apply(libs.pluginsId.kotlin.compose)
        }
    }

    fun androidConfig( project: Project) {
        val javaVer = JavaVersion.valueOf(libs.version.aa.java.compatibility)
        project.android<BaseAppModuleExtension> {
            compileSdk = libs.version.aa.compile.sdk.toInt()
            defaultConfig {
                minSdk = libs.version.aa.min.sdk.toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                targetSdk = libs.version.aa.compile.sdk.toInt()
                versionCode = libs.version.aa.versionCode.toInt()
                versionName = libs.version.aa.versionName
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
                    proguardFiles(
                        getDefaultProguardFile("proguard-android.txt"),
                        "proguard-rules.pro"
                    )
                    signingConfig = signingConfigs.getByName("debug")
                }
            }
            compileOptions {
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = javaVer
                targetCompatibility = javaVer
            }
            lint {
                checkReleaseBuilds = false
            }
            project.tasks.withType<KotlinCompile>().configureEach {
                compilerOptions.languageVersion
                    .set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
            }
        }
    }

     override fun dependenciesConfig( project: Project) {
        super.dependenciesConfig( project)
         project.dependencies.apply {
             add("implementation", libs.androidx.runtime)
         }
    }
}
