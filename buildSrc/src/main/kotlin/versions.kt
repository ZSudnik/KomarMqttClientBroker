import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.ide.idea.model.Project


object ver {

    object build {
        const val min_sdk = 29
        const val compile_sdk = 34
        const val build_tools = "34.0.0"
        val java_compatibility = JavaVersion.VERSION_17
        const val compose_compiler = "1.5.11"
    }

    object jetbrains {
        const val coroutines = "1.8.0"  // two place change
        const val kotlin: String = "1.9.23" // two place change
    }

    object google {
        const val ksp = "1.9.23-1.0.19"
        const val accompanist_appcompat_theme = "0.27.1"
        const val accompanist_drawablepainter = "0.20.0"
        const val accompanist_webview = "0.30.1"
        const val zxing_core = "3.4.0"
        const val gson = "2.10.1"
        const val exoplayer = "1.3.0"
    }

    object android {
        const val gradle = "8.2.2"
        const val desugar = "2.0.4"
        const val material3 = "1.1.0-beta02"
        const val material = "1.6.1"
    }

    object androidx {
        const val appcompat = "1.6.1"
        const val lifecycle = "2.7.0"
        const val core = "1.12.0"
        const val navigation_compose = "2.8.0-alpha05"//"2.7.6"
        const val activity = "1.6.1"
        const val activity_compose = "1.8.2"
        const val customview_poolingcontainer = "1.0.0"
        const val constraintlayout_compose = "1.0.1"
        const val camerax = "1.3.2"
        const val datastore_preferences = "1.0.0"
    }

    object various {
        const val junit = "4.13.2"
        const val slf4j = "2.0.12"
        const val flow_redux = "1.0.2"
        const val lottie = "6.0.0"
        const val io_netty = "4.1.106.Final"
        const val koin = "3.5.3"
        const val atomicfu = "0.23.2" // two place change
        const val colormath = "3.4.0"
        const val ktor = "2.3.9"
    }
}