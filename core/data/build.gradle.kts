plugins {
    id("musicapp.kmp.library")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
