# Feature: Search

Module: `:feature:search` — package `com.sangtq.musicappkmp.feature.search`

## Purpose

Tìm kiếm track/album/artist trong catalog với query debounce và phân trang cursor.

## Business Flow

- Người dùng gõ query → debounce (~300ms) → gọi API search.
- Kết quả phân nhóm theo loại (tracks/albums/artists), phân trang bằng cursor.
- Chọn track → Player; chọn album/artist → detail (giai đoạn sau).

## User Flow

```
Search ─▶ gõ query ─(debounce)─▶ hiển thị kết quả ─▶ tap track ─▶ Player
        └▶ scroll cuối danh sách ─▶ load thêm (cursor)
```

## Screens

- `SearchScreen` (stateful) + `SearchContent` (stateless): ô tìm kiếm, trạng thái empty/loading/error, danh sách kết quả nhóm.

## Navigation Flow

- Route: `@Serializable data object SearchRoute`.
- `NavGraphBuilder.searchGraph(onTrackClick: (String) -> Unit)`.

## State Flow

```kotlin
data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchItem> = emptyList(),
    val nextCursor: String? = null,
    val error: String? = null,
) : UiState

sealed interface SearchIntent : Intent {
    data class QueryChanged(val value: String) : SearchIntent
    data object LoadMore : SearchIntent
    data class ResultClicked(val item: SearchItem) : SearchIntent
}

sealed interface SearchEffect : Effect {
    data class OpenPlayer(val trackId: String) : SearchEffect
}
```

UseCase: `SearchCatalogUseCase` (trả trang + cursor). Debounce xử lý trong ViewModel bằng `Flow`
(`debounce` operator) trên dòng query.

## API Used

- `GET /search?q={query}&cursor={cursor}` → `SearchPageDto { items, nextCursor }`.

## Related Modules

- `core:network`, `core:data`, `core:designsystem`, `core:ui`. Không phụ thuộc feature khác.

## Known Issues

- Chưa có lịch sử tìm kiếm / gợi ý.
- Chưa cache kết quả search (mỗi query gọi network); cân nhắc cache ngắn hạn sau.
