# CLAUDE.md — AI agent rules for MusicAppKMP

Bạn đang làm việc trong một app **music streaming** xây bằng **Kotlin Multiplatform + Compose
Multiplatform** (Android + iOS). Logic và UI dùng chung ở `commonMain`; code nền tảng cô lập sau
`expect`/`actual`.

> **Đọc trước khi code:** [`docs/README.md`](docs/README.md) → [`docs/AI_AGENT_RULES.md`](docs/AI_AGENT_RULES.md).
> Toàn bộ tài liệu kỹ thuật nằm trong [`docs/`](docs/). File này là bản tóm tắt vận hành.

## Project facts

- Root package: `com.sangtq.musicappkmp`. Kotlin 2.4.0, Compose Multiplatform 1.11.1, Material3, AGP 9.0.1.
- Kiến trúc: **Clean Architecture** (`presentation → domain → data`) + **MVI** + **feature modules** trên **core modules**.
- Stack: **Koin** (DI), **Ktor** + **kotlinx.serialization** (network), **SQLDelight** (cache), **DataStore** + `SecureStorage` (prefs/token), **Coil 3** (ảnh), **Media3/AVPlayer** (playback), **Kermit** (log).
- Backend: **Deezer** qua proxy **RapidAPI** (`deezerdevs-deezer`) — static header auth (`X-RapidAPI-Key`/`X-RapidAPI-Host`), KHÔNG OAuth/refresh, 5 endpoint, playback = preview mp3 ~30s. Key ở `local.properties` (`RAPIDAPI_KEY`), không hardcode. Hợp đồng: [`docs/Api/DeezerApi.md`](docs/Api/DeezerApi.md).

## Output bắt buộc (xem [`docs/AI_AGENT_RULES.md`](docs/AI_AGENT_RULES.md))

- **Trước khi code:** `Requirement Analysis` → `Impact Analysis` → `Execution Plan`.
- **Sau khi code:** `Files Changed` → `Summary` → `Validation` → `Risks`.

## Hard rules (không vi phạm)

1. **Đọc tài liệu trước khi code; tìm implementation tương tự trước khi tạo mới; ưu tiên reuse.**
2. **Không tạo architecture mới, không tạo state management mới, không refactor ngoài phạm vi, không duplicate.**
3. `domain` là Kotlin thuần (không Compose/Ktor/SQLDelight/Android/iOS).
4. **DTO/Entity không ra khỏi layer `data`** — map ở ranh giới (`toDomain()`/`toEntity()`).
5. UI → ViewModel → UseCase → Repository. Composable không gọi Repository/DataSource trực tiếp.
6. **MVI**: 1 `UiState` bất biến + `sealed Intent`/`Effect`; state chỉ đổi trong reducer của `MviViewModel`.
7. Màn hình tách: `XScreen` (stateful) + `XContent` (stateless, `@Preview`).
8. Không ném exception xuyên layer — `data` trả `AppResult.Failure(AppError)`; layer trên xử lý `AppResult`.
9. API nền tảng chỉ qua `expect`/`actual` trong `core:*`. Feature **không** phụ thuộc feature.
10. Không secret/token/PII trong code/log/fixture. Token ở `SecureStorage`. Mặc định đặt code ở `commonMain`.

## Tài liệu theo chủ đề

| Chủ đề | File |
| --- | --- |
| Luật & workflow AI Agent | [`docs/AI_AGENT_RULES.md`](docs/AI_AGENT_RULES.md) |
| Kiến trúc, layer, data flow | [`docs/Architecture.md`](docs/Architecture.md) |
| Tìm/đặt code ở đâu | [`docs/ProjectStructure.md`](docs/ProjectStructure.md) |
| Hợp đồng MVI | [`docs/StateManagement.md`](docs/StateManagement.md) |
| Quy ước code | [`docs/CodingStandards.md`](docs/CodingStandards.md) |
| Networking / auth / refresh | [`docs/NetworkingGuide.md`](docs/NetworkingGuide.md) |
| **API backend (Deezer/RapidAPI)** | [`docs/Api/DeezerApi.md`](docs/Api/DeezerApi.md) |
| Error handling | [`docs/ErrorHandling.md`](docs/ErrorHandling.md) |
| Test | [`docs/TestingGuide.md`](docs/TestingGuide.md) |
| Cách thêm feature | [`docs/Features/README.md`](docs/Features/README.md) |
| Lý do quyết định kiến trúc | [`docs/ADR/`](docs/ADR/) |
| Setup / build / run | [`docs/Setup/`](docs/Setup/) |

## Workflow

- Branch + Conventional Commits + PR. Không commit/push trừ khi được yêu cầu.
- Verify: `./gradlew :androidApp:assembleDebug` + `./gradlew :shared:allTests`.
- Cần quyết định kiến trúc mới → viết ADR ([`docs/ADR/`](docs/ADR/)) thay vì âm thầm lệch hướng.
