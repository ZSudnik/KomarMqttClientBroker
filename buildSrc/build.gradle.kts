import com.electronwill.nightconfig.core.concurrent.StampedConfig
import com.electronwill.nightconfig.core.file.FileConfig
import org.gradle.kotlin.dsl.register

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}
repositories {
    google()
    mavenCentral()
}
allprojects {
    configurations.all {
        resolutionStrategy {
            force(libs.guava)
        }
    }
}

gradlePlugin {
    plugins {
        create("android-library-module") {
            id = "android-library-module"
            implementationClass = "gradlePlugins.AndroidLibraryPlugin"
        }
        create("android-application-module"){
            id = "android-application-module"
            implementationClass = "gradlePlugins.AndroidApplicationPlugin"
        }
        create("codec-module") {
            id = "codec-module"
            displayName = "codec_module"
            implementationClass = "gradlePlugins.CodecPlugin"
            description = "used for java modules"
        }
    }
}

dependencies  {
    implementation(libs.gradle.main)
    implementation(libs.gradle.api)
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlinx.atomicfu)
//    implementation(libs.kotlin.stdlib)
//    implementation(libs.kotlin.reflect)
}
kotlin {
    sourceSets {
        main {
            kotlin.srcDir(layout.buildDirectory.dir("generated/kotlin/out"))
        }
    }
}

val generateBuildSrcCode  = project.tasks.register<GenerateLibTask>("generateBuildSrcCode") {
    tomlFile.convention( rootProject.file("../gradle/libs.versions.toml"))
    project.layout.buildDirectory.file("generated/kotlin/out/GeneratedLibs.kt")
        .let(generatedSourcesFile::convention)
    doLast {
        sourceSets["main"].kotlin.srcDir("build/generated")
    }
}
tasks.named("compileKotlin").configure {
    dependsOn(generateBuildSrcCode)
}

abstract class GenerateLibTask: DefaultTask() {

    @get:Input
    abstract val tomlFile: Property<File>

    @get:OutputFile
    abstract val generatedSourcesFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val sourceCode = generateCode( tomlFile = tomlFile.get() )
        val outputFile = generatedSourcesFile.get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(text = sourceCode.toString())
    }

    private fun generateCode(tomlFile: File): String{
        val sb = StringBuilder()
        val fileConf = FileConfig.of(tomlFile).let {
                it.load()
                it.entrySet()
            }
        sb.appendLine("package gradlePlugin \n")
        sb.appendLine("\nclass Libs() { ")
        var versionMap = mutableMapOf<String, String>()
        fileConf.forEach {
            when (it.key) {
                "versions" -> {
                    val nested = mutableListOf<Trip>()
                    it.getValue<StampedConfig>().entrySet().forEach { entry ->
                        val key = entry.key
                        val value = entry.getValue<String>()
                        versionMap[key] = value
                        addBranchToTree(nested, key.split("-", "_"), mapOf("value" to value))
                    }
                    sb.appendLine("class Version() {")
                    nested.forEach { trip ->
                        sb.append(renderNestedClass(trip, indent = "    "))
                    }
                    sb.appendLine("}")
                    sb.appendLine("val version = Version()")
                }
                "libraries" -> {
                    val nested =  mutableListOf<Trip>()
                    for (entry in it.getValue<StampedConfig>().entrySet()) {
                        val key = entry.key
                        val libObj = entry.getValue() as? StampedConfig ?: continue
                        var libMap = when {
                            (libObj.get<String>("group") != null) -> {
                                val group = libObj.get<String>("group")
                                val name = libObj.get<String>("name") ?: continue
                                var version = libObj.get<String>("version.ref")?.let { versionMap[it] }
                                if( version == null)
                                    version = libObj.get<String>("version") ?: continue
                                mapOf(
                                    "group" to group,
                                    "name" to name,
                                    "version" to version,
                                    "keyAlias" to key
                                )
                            }
                            (libObj.get<String>("module") != null) -> {
                                val moduleParts = libObj.get<String>("module").split(":")
                                if (moduleParts.size != 2) continue
                                val group = moduleParts[0]
                                val name = moduleParts[1]
                                var version = libObj.get<String>("version.ref")?.let { versionMap[it] }
                                if( version == null)
                                    version = libObj.get<String>("version") ?: continue
                                mapOf(
                                    "group" to group,
                                    "name" to name,
                                    "version" to version,
                                    "keyAlias" to key
                                )
                            }
                            else -> continue
                        }
                        addBranchToTree(nested, key.split("-", "_"), libMap)
                    }
                    nested.forEach { trip ->
                        if (trip.branch.isEmpty()) {
                            val libMap = trip.metadata!!
                            val propertyName = trip.name
                            sb.appendLine("""       val $propertyName = mapOf(
                             |           "group" to "${libMap["group"]}",
                             |           "name" to "${libMap["name"]}",
                             |           "version" to "${libMap["version"]}" ) """.trimMargin())
                        } else {
                            sb.append(renderLibraryClass( trip, indent = "    "))
                        }
                    }
                }
                "plugins" -> {
                    val nested = mutableListOf<Trip>()
                    for (plugin in it.getValue<StampedConfig>().entrySet()) {
                        val key = plugin.key
                        val pluginObj = plugin.getValue() as? StampedConfig ?: continue
                        pluginObj.entrySet()
                            .filter { it.key == "id" }
                            .mapNotNull { it.getValue<String>() }
                            .forEach { id ->
                                addBranchToTree(nested, key.split("-", "_"), mapOf("value" to id))
                            }
                    }
                        sb.appendLine("class PluginsId() { ")
                    nested.forEach { trip ->
                        sb.append(renderNestedClass(trip, indent = "    "))
                    }
                    sb.appendLine("}")
                    sb.appendLine("val pluginsId = PluginsId()")
                }
                else -> Unit
            }
        }
        sb.appendLine("}")
        sb.appendLine("val libs = Libs()")
        return sb.toString()
    }

    inner class Trip(
        var name: String? = null,
        var branch: MutableList<Trip> = mutableListOf<Trip>(),
        var metadata: Map<String, String>? = null){
        override fun toString(): String{
            return StringBuilder().apply {
                this.append(" class: $name;")
                this.append(" branch: $branch;")
                this.append(" data: $metadata;")
            }.toString()
        }
    }

    private fun addBranchToTree(nested: MutableList<Trip>, parts: List<String>, metadata: Map<String, String>){
        var tmpNest: MutableList<Trip> = nested
        parts.forEach { part ->
            if (tmpNest.none { it.name == part }) {
                val newBranch = Trip(name = part)
                if (part == parts.last()) newBranch.metadata = metadata
                tmpNest.add(newBranch)
                tmpNest = newBranch.branch
            } else {
                val matchingTrip = tmpNest.find { it.name == part }
                if (part == parts.last()) {
                    matchingTrip?.metadata = metadata
                } else {
                    tmpNest = matchingTrip!!.branch
                }
            }
        }
    }
    private fun renderNestedClass(trip: Trip, indent: String = ""): String {
        val sb = StringBuilder()
        val name = trip.name!!
        val metadata = trip.metadata
        if (trip.branch.isNotEmpty()) {
            if (metadata != null) {
                sb.appendLine(
                    """${indent}class ${name.toClassName()} (private val value: String = "${metadata["value"]}"){
                    |$indent     override fun toString(): String = value
                    |$indent     override fun equals(other: Any?): Boolean {
                    |$indent         return when (other) {
                    |$indent             is String -> value == other
                    |$indent             is ${name.toClassName()} -> value == other.value
                    |$indent             else -> false
                    |$indent         }
                    |$indent     }
                    |$indent     override fun hashCode(): Int = value.hashCode()
                    |$indent     fun toInt(): Int = value.toInt()
                    |""".trimMargin()
                )
            }else {
                sb.appendLine("${indent}class ${name.toClassName()} {")
            }
                val indent2 = "$indent    "
                trip.branch.forEach { newBra ->
                    sb.append(renderNestedClass(newBra, indent2))
                }
                sb.appendLine("$indent}")
                sb.appendLine("$indent val ${name.replaceFirstChar { it.lowercaseChar() }} = ${name.toClassName()}()")
            } else {
            if (metadata != null) {
                sb.appendLine("$indent val $name = \"${metadata["value"]}\"")
            }
        }
        return sb.toString()
    }

    private fun renderLibraryClass(trip: Trip, indent: String = ""): String {
        val sb = StringBuilder()
        val name = trip.name!!
        val libMap = trip.metadata
        if( trip.branch.isNotEmpty()) {
            if (libMap != null) {
                sb.appendLine(
                    """${indent}class ${name.toClassName()} : Map<String, String> {
                    |$indent     private val backingMap = mapOf(
                    |$indent         "group" to "${libMap["group"]}",
                    |$indent         "name" to "${libMap["name"]}",
                    |$indent         "version" to "${libMap["version"]}"    )
                    |$indent     override val entries: Set<Map.Entry<String, String>> = backingMap.entries
                    |$indent     override val keys: Set<String> = backingMap.keys
                    |$indent     override val size: Int = backingMap.size
                    |$indent     override val values: Collection<String> = backingMap.values
                    |$indent     override fun isEmpty(): Boolean = backingMap.isEmpty()
                    |$indent     override fun containsKey(key: String): Boolean = backingMap.containsKey(key)
                    |$indent     override fun containsValue(value: String): Boolean = backingMap.containsValue(value)
                    |$indent     override fun get(key: String): String? = backingMap[key]
                    |""".trimMargin())
            } else {
                sb.appendLine("${indent}class ${name.toClassName()} {")
            }
            val indent2 = "$indent    "
            trip.branch.forEach { newBra ->
                sb.append(renderLibraryClass(newBra, indent2))
            }
            sb.appendLine("$indent }")
            sb.appendLine("$indent val ${name.replaceFirstChar { it.lowercaseChar() }} = ${name.toClassName()}()")
        }else{
            if (libMap != null)
            sb.appendLine("""$indent     val $name = mapOf(
                        |$indent         "group" to "${libMap["group"]}",
                        |$indent         "name" to "${libMap["name"]}",
                        |$indent         "version" to "${libMap["version"]}"  )
                        |""".trimMargin())
        }
        return sb.toString()
    }

    private fun String.toClassName(): String =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

}