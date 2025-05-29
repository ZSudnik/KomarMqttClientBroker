plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(libs.androidx.activity)
            api(libs.kotlin.stdlib)
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation( libs.androidx.core)
                implementation( libs.androidx.navigation.compose)
                api(libs.androidx.lifecycle.common)
                implementation(libs.coroutines.core)
            }
        }
    }
}
android {
    namespace = "com.zibi.mod.common.livecycle"
    resourcePrefix = "common_navigation"
}
