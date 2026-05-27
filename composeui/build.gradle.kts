import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatformAndroidLibrary)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.composeMultiplatform)
    alias(libs.plugins.kotlin.composeHotReload)
}

kotlin {
    jvm()
    android {
        namespace = "ru.otus.mvi.composeui"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        androidResources.enable = true

        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "common"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":common"))
                api(libs.compose.multiplatform.runtime)
                api(libs.compose.multiplatform.foundation)
                api(libs.compose.multiplatform.material3)
                api(libs.compose.multiplatform.material3.icons)
                api(libs.compose.multiplatform.ui)
                implementation(libs.compose.multiplatform.components.resources)
                implementation(libs.compose.multiplatform.uiToolingPreview)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.compose.multiplatform.uiToolingPreview)
                implementation(libs.androidx.activity.compose)
            }
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "ru.otus.mvi.composeui"
    generateResClass = always
}

