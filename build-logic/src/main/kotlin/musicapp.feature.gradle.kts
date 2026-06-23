import org.gradle.api.artifacts.VersionCatalogsExtension

/**
 * Convention cho feature module: KMP library + Compose + bộ dependency dùng chung mọi feature
 * (core:common/ui/designsystem, catalog, koin, compose UI, lifecycle). Module chỉ cần set
 * `namespace` và thêm dependency đặc thù (vd core:playback, core:database). Xem ADR-0009.
 */
plugins {
    id("musicapp.kmp.library.compose")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:ui"))
            implementation(project(":core:designsystem"))
            implementation(project(":catalog"))
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
            implementation(libs.findLibrary("koin-core").get())
            implementation(libs.findLibrary("koin-compose-viewmodel").get())
            implementation(libs.findLibrary("compose-runtime").get())
            implementation(libs.findLibrary("compose-foundation").get())
            implementation(libs.findLibrary("compose-material3").get())
            implementation(libs.findLibrary("compose-ui").get())
            implementation(libs.findLibrary("androidx-lifecycle-viewmodelCompose").get())
            implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
        }
    }
}
