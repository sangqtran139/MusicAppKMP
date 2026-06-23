plugins {
    id("musicapp.kmp.library")
    alias(libs.plugins.sqldelight)
}

// SQLDelight: schema `.sq` ở src/commonMain/sqldelight; sinh API Kotlin có kiểu.
// Driver theo nền tảng được cấp qua platformModule() ở :shared (xem docs/ADR/0007).
sqldelight {
    databases {
        create("MusicDatabase") {
            packageName.set("com.sangtq.musicappkmp.core.database")
        }
    }
}

kotlin {
    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.database"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            api(libs.sqldelight.runtime)
            api(libs.sqldelight.coroutines)
        }
    }
}
