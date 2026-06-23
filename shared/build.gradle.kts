import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

// SQLDelight: schema `.sq` ở src/commonMain/sqldelight; sinh API Kotlin có kiểu vào package này.
// Driver theo nền tảng được cấp qua platformModule() (xem docs/ADR/0007).
sqldelight {
    databases {
        create("MusicDatabase") {
            packageName.set("com.sangtq.musicappkmp.core.database")
        }
    }
}

// RAPIDAPI_KEY: đọc từ local.properties (gitignored) hoặc biến môi trường, sinh ra một
// object Kotlin lúc build. KHÔNG hardcode key trong source. Xem docs/NetworkingGuide.md.
val rapidApiKey: String = run {
    val props = Properties()
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
    props.getProperty("RAPIDAPI_KEY") ?: System.getenv("RAPIDAPI_KEY") ?: ""
}

val generateBuildKonfig = tasks.register("generateBuildKonfig") {
    val outDir = layout.buildDirectory.dir("generated/buildkonfig/kotlin")
    outputs.dir(outDir)
    val key = rapidApiKey
    inputs.property("rapidApiKey", key)
    doLast {
        val pkgDir = outDir.get().asFile.resolve("com/sangtq/musicappkmp/core/config")
        pkgDir.mkdirs()
        pkgDir.resolve("BuildKonfig.kt").writeText(
            """
            package com.sangtq.musicappkmp.core.config

            internal object BuildKonfig {
                const val RAPIDAPI_KEY: String = "$key"
            }
            """.trimIndent() + "\n"
        )
    }
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
        commonMain {
            kotlin.srcDir(generateBuildKonfig)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.media3.exoplayer)
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
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
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
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
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