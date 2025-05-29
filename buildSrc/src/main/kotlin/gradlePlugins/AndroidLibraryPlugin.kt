package gradlePlugins

import com.android.build.gradle.BaseExtension
import gradlePlugin.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryPlugin : BasePlugin() {

    private val Project.android: BaseExtension //BaseAppModuleExtension //BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
            ?: error("Not an Android module: $name")

    override fun apply(project: Project) =
        with(project) {
            applyPlugins( project)
            androidConfig()
            dependenciesConfig( project)
        }

    override fun applyPlugins( project: Project) {
        super.applyPlugins( project)
        project.plugins.run {
            apply(libs.pluginsId.kotlin.multiplatform)
            apply(libs.pluginsId.android.library)
            apply(libs.pluginsId.kotlin.compose)
        }
    }

    private fun Project.androidConfig() {
        val javaVer = JavaVersion.valueOf(libs.version.aa.java.compatibility)
        android.run {
            compileSdkVersion( libs.version.aa.compile.sdk.toInt())
            defaultConfig {
                minSdk = libs.version.aa.min.sdk.toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            buildTypes {
                getByName("debug") {
                    isMinifyEnabled = false
                }
                getByName("release") {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            compileOptions {
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = javaVer
                targetCompatibility = javaVer
            }
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
                }
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