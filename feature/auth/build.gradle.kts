plugins {
    id("musicapp.kmp.library.compose")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.feature.auth"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
        }
    }
}
