plugins {
    id("musicapp.kmp.library.compose")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.designsystem"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.coil.compose)
        }
    }
}
