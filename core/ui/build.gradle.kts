plugins {
    id("musicapp.kmp.library")
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
        }
    }
}
