# Setup

Hướng dẫn dựng môi trường, build và chạy MusicAppKMP.

- [LocalDevelopment.md](LocalDevelopment.md) — cài đặt môi trường, công cụ, chạy app khi dev.
- [BuildGuide.md](BuildGuide.md) — build artifact, release build, và thứ tự bootstrap kiến trúc (Phase 0).

## Yêu cầu tối thiểu

| Công cụ | Phiên bản |
| --- | --- |
| JDK | 17+ (build chạy JVM target 11) |
| Android Studio | bản hỗ trợ AGP 9.0.1 + KMP plugin |
| Xcode | bản mới (cho iОS, chỉ trên macOS) |
| Kotlin | 2.4.0 (qua Gradle) |
| Android SDK | compileSdk 36, minSdk 24 |

## Chạy nhanh

- Android: `./gradlew :androidApp:assembleDebug` rồi cài, hoặc chạy từ run widget của IDE.
- iOS: mở thư mục `iosApp/` trong Xcode và chạy.
