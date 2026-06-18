# BuildGuide

## Build debug

```bash
./gradlew :androidApp:assembleDebug        # APK debug
./gradlew build                            # build + test toàn project
```

iOS: build qua Xcode (mở `iosApp/`) hoặc run configuration iOS. KMP plugin tự build `Shared`
framework (static, `baseName = "Shared"`) cho `iosArm64` và `iosSimulatorArm64`.

## Generate artifacts

- **iOS framework**: được build tự động khi build app iOS; cấu hình ở `shared/build.gradle.kts`
  (`iosTarget.binaries.framework`).
- **Compose Resources**: sinh class `Res` từ `commonMain/composeResources` khi build.
- (Sau khi thêm SQLDelight) **schema/DAO** sinh từ file `.sq` khi build; không sửa code generate.

## Release build

### Android
```bash
./gradlew :androidApp:assembleRelease      # hoặc bundleRelease cho AAB
```
- Hiện `isMinifyEnabled = false`. Trước khi phát hành thật: bật minify/shrink, cấu hình R8/ProGuard
  rules, ký bằng release keystore (không commit keystore/secrets).

### iOS
- Archive trong Xcode (Product → Archive), ký bằng provisioning profile, upload qua Xcode/Transporter.
- Bật background mode "Audio, AirPlay, and Picture in Picture" cho playback nền (xem
  [`../Features/Player.md`](../Features/Player.md)).

## Bootstrap kiến trúc — Phase 0 (biến scaffold thành nền móng)

Làm trước mọi feature, mỗi bước là một PR nhỏ. Chỉ Phase 0 mới được sửa Gradle/`libs.versions.toml`.

1. **Catalog + convention plugins**: thêm thư viện vào `gradle/libs.versions.toml` (Koin, Ktor,
   kotlinx.serialization, SQLDelight, DataStore, Coil, Coroutines, datetime, Kermit, Media3, test libs
   — verify version tương thích Kotlin 2.4.0/CMP 1.11.1). Tạo `build-logic` với
   `musicapp.kmp.library` / `musicapp.kmp.feature`.
2. **`core:common`**: `AppResult`, `AppError`, `DispatcherProvider`, Kermit.
3. **`core:ui`**: `MviViewModel`, `UiState`/`Intent`/`Effect`.
4. **`core:designsystem`**: theme, `AppAsyncImage` (Coil).
5. **`core:network`**: `HttpClient` factory (engine theo nền tảng), JSON, Logging, Auth/refresh, map `AppError`.
6. **`core:datastore`**: DataStore prefs + `SecureStorage` expect/actual.
7. **`core:database`**: SQLDelight plugin + driver expect/actual + migration test.
8. **`core:data`**: `networkBoundResource`.
9. **`core:playback`**: `AudioPlayer` + `AudioPlayerFactory` expect/actual (ExoPlayer/AVPlayer).
10. **`:shared`**: khởi tạo Koin (Android `Application` + bootstrap iOS), NavHost + route contract,
    test Koin `verify()`.

> Có thể giữ các phần này dưới dạng package trong `:shared` trước, rồi tách thành module khi convention
> plugin đã sẵn sàng — miễn build vẫn xanh. Package layout không đổi.

## CI (đề xuất)

- Mỗi PR: `./gradlew build` + `:shared:allTests` + test Koin `verify()`.
- Fail nếu có dependency `feature → feature` (kiểm bằng script ranh giới module).
