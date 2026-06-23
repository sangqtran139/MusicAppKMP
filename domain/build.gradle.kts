plugins {
    id("musicapp.kmp.library")
}

// Domain thuần Kotlin (model + repository interface + use case), độc lập — KHÔNG Ktor/SQLDelight/
// Compose. Chỉ phụ thuộc core:common (AppResult/Resource/DispatcherProvider) + coroutines.
kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
