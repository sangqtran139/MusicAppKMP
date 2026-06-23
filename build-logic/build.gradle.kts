plugins {
    `kotlin-dsl`
}

// Convention plugins (precompiled scripts trong src/main/kotlin/*.gradle.kts) gom cấu hình KMP
// lặp lại giữa các module (xem ADR-0009). Cần plugin JAR trên classpath để DSL biên dịch được.
dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.composeCompiler.gradlePlugin)
    implementation(libs.sqldelight.gradlePlugin)
}
