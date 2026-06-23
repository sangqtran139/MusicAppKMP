plugins {
    id("musicapp.feature")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.feature.player"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.playback)
        }
    }
}
