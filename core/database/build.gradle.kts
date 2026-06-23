import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
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
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
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
