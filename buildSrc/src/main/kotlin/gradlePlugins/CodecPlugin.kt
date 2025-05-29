package gradlePlugins

import gradlePlugin.libs
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class CodecPlugin : BasePlugin() {

    private val Project.kotlinJvm: KotlinJvmProjectExtension
        get() = extensions.findByName("kotlin") as? KotlinJvmProjectExtension
            ?: error("Not an KotlinJvm module: $name")

//    private val Project.java: JavaPluginExtension
//        get() = extensions.findByName("java") as? JavaPluginExtension
//            ?: error("Not an KotlinJvm module: $name")

    override fun apply(project: Project) = with(project) {
        applyPlugins( project)
        javaConfig( project)
        dependenciesConfig( project)
    }

    override fun applyPlugins( project: Project) {
        super.applyPlugins( project)
        project.plugins.run {
            apply(libs.pluginsId.kotlin.jvm)
            apply("kotlinx-atomicfu")
            apply("java-library")

        }
    }

    fun javaConfig( project: Project) {
        project.kotlinJvm.run {
            jvmToolchain {
                languageVersion.set(
                    JavaLanguageVersion.of(libs.version.aa.jdk)
                )
            }
        }
//        val javaVer = JavaVersion.valueOf(libx.findVersion("aa_java_compatibility").get().displayName)
//        project.java.run {
//            sourceCompatibility = javaVer
//            targetCompatibility = javaVer
//            withSourcesJar()
//            withJavadocJar()
//        }
    }

    override fun dependenciesConfig( project: Project) {
        project.dependencies.apply {
            add("api", libs.kotlin.stdlib)
        }
    }
}