import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
        // SQLDelight native driver (SQLiter) cần link system sqlite3. Cờ này do plugin SQLDelight
        // thêm vào module áp plugin (:core:database) — nhưng binary iOS link ở :shared nên thêm tay.
        iosTarget.binaries.all {
            linkerOpts("-lsqlite3")
        }
    }
    
    androidLibrary {
       namespace = "com.sangtq.musicappkmp.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.sqldelight.driver.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.driver.native)
        }
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.ui)
            implementation(projects.core.designsystem)
            implementation(projects.core.data)
            implementation(projects.core.network)
            implementation(projects.core.database)
            implementation(projects.core.playback)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.ktor.client.core)
            implementation(libs.kermit)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
        }
        // Driver JVM in-memory cho Koin graph test chạy trên host (xem KoinModulesTest).
        getByName("androidHostTest").dependencies {
            implementation(libs.sqldelight.driver.sqlite)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}