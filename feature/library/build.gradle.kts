plugins {
    id("musicapp.feature")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.feature.library"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.database)
        }
    }
}
