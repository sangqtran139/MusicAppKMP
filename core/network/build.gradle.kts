import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

// RAPIDAPI_KEY: đọc từ local.properties (gitignored) hoặc biến môi trường, sinh ra một object
// Kotlin lúc build. KHÔNG hardcode key trong source. Xem docs/NetworkingGuide.md.
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
    iosArm64()
    iosSimulatorArm64()

    androidLibrary {
        namespace = "com.sangtq.musicappkmp.core.network"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateBuildKonfig)
        }
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
