# ProjectStructure

Mục tiêu: AI Agent biết **tìm code ở đâu** và **đặt code mới vào đâu**.

## Module Structure

```
:androidApp            Entry point Android (MainActivity, Application, khởi tạo Koin Android)
:iosApp                Entry point iOS (Xcode, ComposeUIViewController) — không phải Gradle module
:shared                Umbrella: gom feature, build iOS framework, NavHost + khởi tạo Koin

:core:common           DispatcherProvider, AppResult, AppError, logging (Kermit), helper
:core:domain           UseCase base, primitive domain dùng chung (PagingResult, SortOrder)
:core:designsystem     Theme, color, typography, spacing, component tái dùng, AppAsyncImage (Coil)
:core:ui               MVI base (MviViewModel, UiState/Intent/Effect), Compose helper
:core:network          HttpClient factory (Ktor), Auth/refresh, ContentNegotiation, map lỗi network
:core:database         SQLDelight setup, driver (expect/actual), DAO dùng chung
:core:datastore        DataStore (preferences) + SecureStorage (expect/actual) cho token
:core:data             Base repository, networkBoundResource (offline-first)
:core:playback         AudioPlayer (expect/actual), PlaybackState, quản lý queue

:feature:auth          Đăng nhập / session
:feature:home          Trang chủ / browse
:feature:search        Tìm kiếm catalog
:feature:library       Thư viện: bài đã lưu, playlist, downloads
:feature:player        Now-playing + mini-player
```

**Chiều phụ thuộc**: `feature:* → core:* → (Kotlin)`. Feature không phụ thuộc feature. Core có thể
phụ thuộc core thấp hơn (`core:data → core:network`, `core:database`) nhưng không phụ thuộc feature.

> Hiện tại repo chỉ có `:androidApp` và `:shared`. Module được tách dần (xem
> [`Setup/BuildGuide.md`](Setup/BuildGuide.md)); trước khi tách, cùng package layout này nằm trong
> `:shared`.

## Package Structure

Root package: `com.sangtq.musicappkmp`. Mỗi module sở hữu một sub-package.

```
com.sangtq.musicappkmp
├── core
│   ├── common · designsystem · ui · network · database · datastore · data · playback
└── feature
    └── <feature>            ví dụ: player
        ├── domain
        │   ├── model         // Track, PlaybackQueue (nếu cục bộ feature)
        │   ├── repository    // TrackRepository (interface)
        │   └── usecase       // PlayTrackUseCase, ToggleFavoriteUseCase
        ├── data
        │   ├── remote        // PlayerApi, dto/  (TrackDto)
        │   ├── local         // PlayerDao (SQLDelight)
        │   ├── mapper        // PlayerMappers.kt
        │   └── repository    // PlayerRepositoryImpl
        ├── presentation
        │   ├── PlayerContract.kt   // UiState, Intent, Effect
        │   ├── PlayerViewModel.kt
        │   ├── PlayerScreen.kt     // stateful
        │   ├── PlayerContent.kt    // stateless, @Preview
        │   └── components/         // composable cục bộ màn hình
        └── di
            └── PlayerModule.kt     // Koin module
```

## Folder Structure (source set của một KMP module)

```
<module>/src/
├── commonMain/kotlin/...          // >90% code ở đây
├── commonTest/kotlin/...          // unit test dùng chung
├── androidMain/kotlin/...         // actual cho Android
├── androidUnitTest/kotlin/...     // test JVM cần type Android
├── iosMain/kotlin/...             // actual cho iOS
└── commonMain/composeResources/   // string, ảnh (Compose Resources)
```

Quy tắc: đặt code ở `commonMain` mặc định. Chỉ chuyển sang `androidMain`/`iosMain` khi chạm API nền
tảng, và phải qua `expect`/`actual`.

## Trách nhiệm từng module (tra cứu nhanh)

| Bạn cần thêm… | Đặt vào… |
| --- | --- |
| Khái niệm nghiệp vụ (vd `Track`) | `feature:<x>/domain/model` (hoặc `core:domain` nếu dùng chung) |
| Cấu trúc JSON response | `feature:<x>/data/remote/dto`, `@Serializable`, suffix `Dto` |
| Màn hình mới | `feature:<x>/presentation` (`Screen` + `Content` + `ViewModel` + `Contract`) |
| Button/card tái dùng | `core:designsystem` |
| Concern HTTP toàn app | `core:network` |
| Gọi API nền tảng (player, keychain, sensor) | `expect` ở `core:*` + `actual` mỗi nền tảng |
| Koin binding mới | `feature:<x>/di/<X>Module.kt`, rồi đăng ký ở `:shared` |
| Destination điều hướng mới | route ở `feature:<x>`, wire vào NavHost ở `:shared` |

## Quy ước build (convention plugins)

Để tránh lặp cấu hình KMP ở mọi module, dùng included build `build-logic` với convention plugins
(`musicapp.kmp.library`, `musicapp.kmp.feature`). File build của một feature thu gọn còn:

```kotlin
plugins { id("musicapp.kmp.feature") }
// chỉ khai báo dependency tới core/feature module
```

Chi tiết: [ADR-0009](ADR/0009-feature-modularization.md) và [`Setup/BuildGuide.md`](Setup/BuildGuide.md).
