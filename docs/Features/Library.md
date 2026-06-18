# Feature: Library

Module: `:feature:library` — package `com.sangtq.musicappkmp.feature.library`

## Purpose

Thư viện cá nhân: track đã lưu (favorites), playlist, và (giai đoạn sau) nhạc đã tải offline.
Offline-first — SQLDelight là nguồn sự thật, đồng bộ với API.

## Business Flow

- Lưu/bỏ lưu track (favorite) → cập nhật local DB ngay (optimistic) + đồng bộ API.
- Danh sách library quan sát từ DB; refresh từ API khi mở hoặc pull-to-refresh.
- Playlist: xem danh sách; tạo/sửa playlist ở giai đoạn sau.

## User Flow

```
Library ─▶ tab "Saved" | "Playlists" ─▶ tap track ─▶ Player
        └▶ toggle favorite ─(optimistic)─▶ cập nhật DB + API
```

## Screens

- `LibraryScreen` (stateful) + `LibraryContent` (stateless): tab Saved/Playlists, danh sách, empty state.

## Navigation Flow

- Route: `@Serializable data object LibraryRoute`.
- `NavGraphBuilder.libraryGraph(onTrackClick: (String) -> Unit, onPlaylistClick: (String) -> Unit)`.

## State Flow

```kotlin
data class LibraryUiState(
    val isLoading: Boolean = false,
    val savedTracks: List<Track> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val selectedTab: LibraryTab = LibraryTab.Saved,
    val error: String? = null,
) : UiState

sealed interface LibraryIntent : Intent {
    data object Load : LibraryIntent
    data class TabSelected(val tab: LibraryTab) : LibraryIntent
    data class ToggleFavorite(val trackId: String) : LibraryIntent
    data class TrackClicked(val trackId: String) : LibraryIntent
}

sealed interface LibraryEffect : Effect {
    data class OpenPlayer(val trackId: String) : LibraryEffect
    data class ShowError(val message: String) : LibraryEffect
}
```

UseCase: `ObserveLibraryUseCase`, `ToggleFavoriteUseCase`, `ObservePlaylistsUseCase`.

## API Used

- `GET /me/favorites`, `PUT/DELETE /me/favorites/{id}`
- `GET /me/playlists`

## Related Modules

- `core:database` (nguồn sự thật), `core:data` (`networkBoundResource`), `core:network`, `core:designsystem`, `core:ui`. Điều hướng sang Player qua route.

## Known Issues

- Tạo/sửa/sắp xếp playlist chưa làm (giai đoạn sau).
- Downloads offline (`DownloadManager` expect/actual, media mã hóa) thuộc giai đoạn sau — sẽ dùng lại cùng `MediaItem` interface của Player.
- Cần xử lý rollback khi optimistic toggle favorite thất bại đồng bộ API.
