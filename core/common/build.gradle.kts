plugins {
    id("musicapp.kmp.library")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
