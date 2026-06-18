# CodingStandards

Quy ước được enforce ở review và trong [`AI_AGENT_RULES.md`](AI_AGENT_RULES.md). Mọi ví dụ dưới đây
là Kotlin thực tế.

## Naming Convention

| Loại | Quy ước | Ví dụ |
| --- | --- | --- |
| Model (domain) | danh từ, không suffix | `Track`, `Playlist` |
| DTO | suffix `Dto` | `TrackDto` |
| Entity / bảng SQLDelight | suffix `Entity` khi map type | `TrackEntity` |
| Repository | `*Repository` / `*RepositoryImpl` | `TrackRepository` |
| UseCase | cụm động từ + `UseCase`, có `operator fun invoke` | `GetTrackUseCase` |
| RemoteDataSource | `*Api` hoặc `*RemoteDataSource` | `CatalogApi` |
| DAO | `*Dao` | `TrackDao` |
| ViewModel | `*ViewModel` | `PlayerViewModel` |
| MVI types | `*UiState`, `*Intent`, `*Effect` | `PlayerUiState` |
| Composable | `*Screen` (stateful) + `*Content` (stateless) | `PlayerScreen`, `PlayerContent` |
| Koin module | `*Module` (val) | `playerModule` |
| Mapper | hàm extension `toDomain()` / `toEntity()` / `toDto()` | `TrackDto.toDomain()` |

## Folder & File Convention

- Folder theo layer: `domain/`, `data/`, `presentation/`, `di/` (xem [`ProjectStructure.md`](ProjectStructure.md)).
- **Một public class một file.** Các thành viên của một MVI contract (`UiState`/`Intent`/`Effect`) được phép chung file `*Contract.kt`.
- Mapper của một feature gom trong `*Mappers.kt`.
- Tên file = tên class.

## Class Convention

```kotlin
// ĐÚNG: domain model bất biến, không annotation framework
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val artworkUrl: String?,
)

// SAI: domain model dính serialization → rò rỉ chi tiết data layer
@Serializable                                   // ❌ không được ở domain
data class Track(val id: String, val title: String)
```

```kotlin
// ĐÚNG: DTO ở data, có @Serializable, suffix Dto
@Serializable
data class TrackDto(
    val id: String,
    val title: String,
    @SerialName("artist_name") val artist: String,
    val artwork: String?,
)
```

- Ưu tiên `val` hơn `var`, immutable collection (`List`) ở public API.
- `sealed interface` cho tập đóng (Intent, Effect, AppError).
- `when` trên sealed type **không** có nhánh `else` — để compiler ép xử lý đủ.

## Function Convention

```kotlin
// ĐÚNG: UseCase một hành động, invoke, trả AppResult
class GetTrackUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(id: String): AppResult<Track> = repository.getTrack(id)
}

// SAI: UseCase ôm nhiều việc + ném exception
class TrackUseCase(private val repo: TrackRepository) {
    suspend fun getAndPlayAndLog(id: String): Track {   // ❌ nhiều trách nhiệm
        return repo.getTrack(id) ?: throw NotFoundException()   // ❌ ném exception xuyên layer
    }
}
```

- Hàm nhỏ, một trách nhiệm. Ưu tiên expression body.
- Suspend function phải main-safe (tự chuyển dispatcher bên trong khi cần).
- Inject `DispatcherProvider`, **không** hardcode `Dispatchers.IO`:

```kotlin
// ĐÚNG
class TrackRepositoryImpl(
    private val api: CatalogApi,
    private val dao: TrackDao,
    private val dispatchers: DispatcherProvider,
) : TrackRepository {
    override suspend fun getTrack(id: String): AppResult<Track> = withContext(dispatchers.io) { /* ... */ }
}

// SAI
withContext(Dispatchers.IO) { /* ... */ }     // ❌ khó test, không inject được
```

## Compose Convention

```kotlin
// ĐÚNG: tách stateful / stateless
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlayerContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
private fun PlayerContent(state: PlayerUiState, onIntent: (PlayerIntent) -> Unit) { /* ... */ }

// SAI: truyền thẳng ViewModel xuống component con
@Composable
fun PlayerButtons(viewModel: PlayerViewModel) { /* ❌ */ }
```

- Hoist state; truyền `(Intent) -> Unit` xuống, không truyền ViewModel.
- Dùng token của `core:designsystem` (color/typography/spacing) — **không** hardcode màu hay `dp` cho giá trị theme.
- Mọi `*Content` phải `@Preview`-able với state mẫu.

## Strings & Resources

- Chuỗi hiển thị cho người dùng đi qua **Compose Resources** (`commonMain/composeResources`), không hardcode literal — để mở đường localization.

## Logging

- Dùng **Kermit**, tag theo class. Không `println`. Không log token, URL có credential, hay PII.

## Định nghĩa "Done" cho mỗi thay đổi

- [ ] Tuân thủ Dependency Rule; không rò rỉ DTO/Entity.
- [ ] Logic mới có test đúng layer (xem [`TestingGuide.md`](TestingGuide.md)).
- [ ] Có Koin binding và graph `verify()` được.
- [ ] `./gradlew build` pass.
- [ ] Chuỗi localizable; không hardcode giá trị theme.
