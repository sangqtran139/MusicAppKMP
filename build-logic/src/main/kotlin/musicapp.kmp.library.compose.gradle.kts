import org.gradle.api.artifacts.VersionCatalogsExtension

/**
 * Convention cho KMP library module có Compose UI (kế thừa [musicapp.kmp.library]).
 * Kèm `ui-tooling-preview` để mọi màn có thể khai báo `@Preview` (rule #7) và `ui-tooling`
 * (renderer) trên target Android để Android Studio render được preview đặt ở commonMain.
 */
plugins {
    id("musicapp.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("compose-uiToolingPreview").get())
        }
        androidMain.dependencies {
            implementation(libs.findLibrary("compose-uiTooling").get())
        }
    }
}
