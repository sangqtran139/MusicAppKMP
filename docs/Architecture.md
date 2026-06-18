# Architecture

## Kiến trúc tổng thể

MusicAppKMP dùng **Clean Architecture** với 3 layer (`presentation → domain → data`), áp dụng
**MVI** ở layer presentation, tổ chức thành **feature modules** đứng trên **core modules**. Code mặc
định nằm ở `commonMain`; phần phụ thuộc nền tảng được cô lập sau `expect`/`actual`.

```
┌────────────────────────────────────────────────────────────────┐
│ App entry points                                                 │
│   androidApp (Activity)        iosApp (ComposeUIViewController)   │
└──────────────┬──────────────────────────────┬──────────────────┘
               │                                │
        ┌──────▼────────────────────────────────▼──────┐
        │ :shared  — khởi tạo Koin + Navigation root    │
        └──────┬────────────────────────────────┬───────┘
               │                                  │
   ┌───────────▼───────────┐        ┌────────────▼──────────────┐
   │ feature:*              │ ─────▶ │ core:*                     │
   │  auth, home, search,   │        │  common, ui (MVI base),    │
   │  player, library       │        │  designsystem, network,    │
   │                        │        │  database, datastore,      │
   │                        │        │  data, playback            │
   └────────────────────────┘        └────────────────────────────┘
```

## Layer Responsibility

### presentation
- Compose UI + MVI ViewModel.
- Chứa: `XxxScreen` (stateful), `XxxContent` (stateless), `XxxViewModel`, `XxxContract.kt` (`UiState`/`Intent`/`Effect`).
- Trách nhiệm: render `UiState`, nhận `Intent`, gọi UseCase, reduce kết quả vào state, phát `Effect` one-shot.
- **Không** chứa logic nghiệp vụ, không gọi Repository/DataSource trực tiếp, không biết DTO/Entity.

### domain
- Kotlin thuần. Chứa: Model (`data class`), Repository **interface**, UseCase.
- Trách nhiệm: định nghĩa nghiệp vụ và hợp đồng dữ liệu.
- **Không** import Compose / Ktor / SQLDelight / Android / iOS. Chỉ Kotlin stdlib + Coroutines.

### data
- Implement Repository interface của `domain`.
- Chứa: DTO (`@Serializable`), RemoteDataSource (Ktor), DAO (SQLDelight), Mapper, RepositoryImpl.
- Trách nhiệm: lấy/lưu dữ liệu (network + cache), quyết định offline-first, map DTO/Entity ↔ Model,
  bắt exception và trả `AppResult.Failure(AppError)`.
- **DTO/Entity tuyệt đối không ra khỏi layer này.**

## Data Flow

Ví dụ thực tế: người dùng nhấn "play".

```
User tap "Play"
   │  PlayerIntent.PlayClicked
   ▼
PlayerViewModel ──gọi──▶ PlayTrackUseCase ──gọi──▶ TrackRepository (interface)
   │ setState{ copy(...) }                                  │ implement
   ▼                                          TrackRepositoryImpl
StateFlow<PlayerUiState>                          ├─ RemoteDataSource (Ktor)
   │ collectAsStateWithLifecycle()                 ├─ TrackDao (SQLDelight)
   ▼                                               └─ Mapper (Dto/Entity → Track)
PlayerScreen / PlayerContent (render)                       │
   ▲ Effect (navigate, snackbar) — one-shot qua SharedFlow  │ trả AppResult<Track>
   └─────────────────────────────────────────────────────────┘
```

Quy tắc dòng chảy:
- Đọc state: `data` (SQLDelight) là **single source of truth**; network chỉ cập nhật DB; UI quan sát DB.
- Ghi: UI phát `Intent` → ViewModel → UseCase → Repository → (network + DB).
- Mọi kết quả băng qua layer đều là **domain Model** bọc trong `AppResult`, không bao giờ là DTO/Entity hay exception thô.

## Dependency Rule

```
presentation ──▶ domain ◀── data
   feature:* ──▶ core:*  (core thấp hơn: core:data → core:network, core:database)
```

- Layer ngoài biết layer trong; **không bao giờ ngược lại**.
- `domain` không phụ thuộc gì ngoài Kotlin + Coroutines.
- `feature` **không** phụ thuộc `feature` khác.
- Vi phạm Dependency Rule phải fail ở compile-time (ranh giới module), không chỉ ở review.

Ví dụ **đúng**: `PlayerRepositoryImpl` (data) implement `TrackRepository` (domain).
Ví dụ **sai**: `domain` import `io.ktor.*` hoặc `PlayerScreen` gọi thẳng `TrackRepositoryImpl`.

## Navigation Strategy

- Dùng **Navigation Compose Multiplatform** với route **type-safe** (`@Serializable`).
- Mỗi feature expose route + extension `NavGraphBuilder.xxxGraph(...)`.
- **NavHost và lắp ráp graph nằm ở `:shared`** — nơi duy nhất biết toàn bộ bản đồ điều hướng.
- Điều hướng giữa các feature dùng route/callback có kiểu, **không** để feature phụ thuộc feature.
- Chi tiết: [ADR-0008](ADR/0008-navigation.md).

## State Management Strategy

- Pattern duy nhất: **MVI**. Mỗi màn hình có đúng 1 `UiState` bất biến, một `sealed interface Intent`,
  một `sealed interface Effect`.
- State đổi **chỉ** trong reducer của `MviViewModel` (`setState { copy(...) }`).
- State liên tục dùng `StateFlow`; sự kiện one-shot (navigate, snackbar) dùng `SharedFlow`/`Channel` qua `Effect`.
- Composable là hàm thuần của `UiState` + callback `(Intent) -> Unit`.
- Chi tiết và code mẫu: [`StateManagement.md`](StateManagement.md).

## Trade-offs đã chấp nhận

- **Nhiều boilerplate** (DTO/Entity/Model + Mapper + UseCase): chi phí trả trước, máy móc, đổi lại độ cô lập và testability. Xem [ADR-0003](ADR/0003-clean-architecture-layering.md), [ADR-0004](ADR/0004-mvi-presentation-pattern.md).
- **Nhiều module**: clean build chậm hơn nhưng incremental build nhanh hơn; enforce Dependency Rule. Xem [ADR-0009](ADR/0009-feature-modularization.md).
- **2 implementation playback** (`expect/actual`): đổi lại tích hợp OS chuẩn. Xem [ADR-0010](ADR/0010-audio-playback-expect-actual.md).
