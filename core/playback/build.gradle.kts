plugins {
    id("musicapp.kmp.library")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.playback"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.media3.exoplayer)
        }
    }
}
