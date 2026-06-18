# Feature: Home

Module: `:feature:home` — package `com.sangtq.musicappkmp.feature.home`

## Purpose

Trang chủ/browse: hiển thị các hàng nội dung được tuyển chọn (new releases, recommended, top
charts). Offline-first — render ngay từ cache, refresh ngầm từ network.

## Business Flow

- Lấy danh sách "section" (mỗi section là một hàng track/album/playlist) từ API.
- Cache vào SQLDelight; UI quan sát DB làm single source of truth; network chỉ cập nhật DB.
- Chọn một item → điều hướng sang Player (track) hoặc detail (album/playlist — giai đoạn sau).

## User Flow

```
Home ─▶ scroll các section ─▶ tap track ─▶ Player (phát) | tap album/playlist ─▶ Detail (sau)
      └▶ pull-to-refresh ─▶ refresh từ network
```

## Screens

- `HomeScreen` (stateful) + `HomeContent` (stateless): danh sách section dạng `LazyColumn`, mỗi
  section là `LazyRow` các card (dùng `AppAsyncImage` từ `core:designsystem`).

## Navigation Flow

- Route: `@Serializable data object HomeRoute`.
- `NavGraphBuilder.homeGraph(onTrackClick: (String) -> Unit, onAlbumClick: (String) -> Unit)`.

## State Flow

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val sections: List<HomeSection> = emptyList(),
    val error: String? = null,
) : UiState

sealed interface HomeIntent : Intent {
    data object Load : HomeIntent
    data object Refresh : HomeIntent
    data class TrackClicked(val trackId: String) : HomeIntent
}

sealed interface HomeEffect : Effect {
    data class OpenPlayer(val trackId: String) : HomeEffect
    data class ShowError(val message: String) : HomeEffect
}
```

UseCase: `ObserveHomeSectionsUseCase`, `RefreshHomeUseCase`.

## API Used

- `GET /home` → danh sách section (`HomeSectionDto`, mỗi section chứa `List<TrackDto|AlbumDto>`).

## Related Modules

- `core:data` (`networkBoundResource`), `core:database` (cache), `core:designsystem` (card, `AppAsyncImage`), `core:network`, `:feature:player` (qua route, không phụ thuộc trực tiếp).

## Known Issues

- Phân trang section dài chưa làm (hiện lấy số lượng cố định).
- Chính sách invalidate cache còn đơn giản (refresh khi rỗng hoặc pull-to-refresh).
