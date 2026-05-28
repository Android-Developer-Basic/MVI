import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":composeui"))
    implementation(project(":statemachine:shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
}

compose.desktop {
    application {
        mainClass = "ru.otus.mvi.statemachine.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.otus.mvi.statemachine"
            packageVersion = "1.0.0"
        }
    }
}