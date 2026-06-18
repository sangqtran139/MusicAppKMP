# LocalDevelopment

## Setup môi trường

1. Cài **JDK 17+** và đặt `JAVA_HOME`.
2. Cài **Android Studio** (bản hỗ trợ AGP 9.0.1) + plugin **Kotlin Multiplatform**.
3. (macOS, cho iOS) cài **Xcode** + command line tools (`xcode-select --install`).
4. Cấu hình `local.properties` (đã có sẵn) trỏ `sdk.dir` tới Android SDK.
5. Mở project bằng Android Studio; để Gradle sync hoàn tất (lần đầu tải KMP/CMP klibs nên hơi lâu).

## Cấu trúc nguồn cần biết

- Code dùng chung: `shared/src/commonMain/kotlin/...`
- Code Android: `shared/src/androidMain/...`, app: `androidApp/`
- Code iOS: `shared/src/iosMain/...`, app: `iosApp/`
- Tài liệu kiến trúc: `docs/` (đọc [`../README.md`](../README.md) trước).

## Run project

### Android
- Từ IDE: chọn run configuration `androidApp` và Run.
- CLI: `./gradlew :androidApp:assembleDebug` rồi cài APK ở `androidApp/build/outputs/apk/debug/`.

### iOS
- Mở `iosApp/` trong Xcode, chọn simulator, Run.
- Hoặc dùng run configuration iOS do KMP plugin tạo trong Android Studio.

## Chạy test

```bash
./gradlew :shared:allTests                 # tất cả test (common + platform)
./gradlew :shared:testDebugUnitTest        # Android JVM unit test
./gradlew :shared:iosSimulatorArm64Test    # iOS simulator test
```

Xem [`../TestingGuide.md`](../TestingGuide.md) để biết test gì ở layer nào.

## Mẹo phát triển

- Sau khi đổi dependency/version trong `gradle/libs.versions.toml`, sync lại Gradle.
- Compose Preview: viết preview trên các `*Content` (stateless) với state mẫu.
- Khi thêm code chạm API nền tảng, tạo `expect` ở `commonMain` + `actual` ở `androidMain`/`iosMain` — đừng nhét `if (Android)` vào common.
- Bật Kermit logger ở debug; không log token/PII (xem [`../ErrorHandling.md`](../ErrorHandling.md)).
