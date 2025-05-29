plugins {
    id("android-library-module")
}

kotlin {
    androidTarget("android"){
        dependencies{
            api(project(":common-lib:lifecycle"))
            api(libs.androidx.activity)
            api(libs.kotlin.stdlib)
            implementation(libs.annotation)
            }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation( libs.androidx.appcompat)
                implementation( libs.androidx.navigation.compose)
            }
        }
    }
}
android {
    namespace = "com.zibi.mod.common.permission"
    resourcePrefix = "common_permission"
}
