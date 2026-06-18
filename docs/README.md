# MusicAppKMP — Tài liệu kỹ thuật

> Đây là file **AI Agent phải đọc đầu tiên**. Sau đó bắt buộc đọc [`AI_AGENT_RULES.md`](AI_AGENT_RULES.md).

## Project Overview

MusicAppKMP là ứng dụng **music streaming** xây dựng bằng **Kotlin Multiplatform + Compose
Multiplatform**, chạy trên Android và iOS, chia sẻ phần lớn UI và logic ở `commonMain`. Dữ liệu
catalog (track/album/artist/playlist) lấy từ **Deezer** qua proxy **RapidAPI**; playback hiện là
**preview mp3 ~30s** (Deezer không cấp full stream qua proxy). Có cache metadata offline.
Hợp đồng API: [`Api/DeezerApi.md`](Api/DeezerApi.md).

- Root package: `com.sangtq.musicappkmp`
- Trạng thái: repo hiện là scaffold mặc định của KMP wizard. Tài liệu này mô tả **nền móng đích**;
  thứ tự bootstrap nằm ở [`Setup/BuildGuide.md`](Setup/BuildGuide.md) và roadmap Phase 0 trong
  [`Features/README.md`](Features/README.md).

## Tech Stack

| Lĩnh vực | Lựa chọn | Ghi chú |
| --- | --- | --- |
| Ngôn ngữ | Kotlin Multiplatform 2.4.0 | Code chung ở `commonMain` |
| UI | Compose Multiplatform 1.11.1 + Material3 | UI dùng chung Android + iOS |
| Kiến trúc | Clean Architecture (`presentation → domain → data`) | Xem [`Architecture.md`](Architecture.md) |
| Presentation pattern | **MVI** | 1 `UiState` + `Intent` + `Effect`, xem [`StateManagement.md`](StateManagement.md) |
| State | Coroutines + `StateFlow` / `SharedFlow` | `viewModelScope` |
| Dependency Injection | Koin | Đa nền tảng |
| Networking | Ktor Client + kotlinx.serialization | Xem [`NetworkingGuide.md`](NetworkingGuide.md) |
| Backend / API | Deezer qua RapidAPI (`deezerdevs-deezer`) | Static header auth, xem [`Api/DeezerApi.md`](Api/DeezerApi.md) |
| Local DB / cache | SQLDelight (DAO + Entity) | offline-first |
| Preferences / token | DataStore + `SecureStorage` (expect/actual) | Keystore / Keychain |
| Navigation | Navigation Compose Multiplatform (type-safe) | ADR-0008 |
| Ảnh | Coil 3 | ADR-0011 |
| Audio playback | Media3 ExoPlayer (Android) / AVPlayer (iOS) qua expect/actual | ADR-0010 |
| Logging | Kermit | |
| Test | kotlin.test, Turbine, MockK, koin-test | Xem [`TestingGuide.md`](TestingGuide.md) |

## Architecture Summary

```
presentation (Compose + MVI ViewModel)
      │  gọi UseCase, nhận AppResult
      ▼
domain (Kotlin thuần: Model, Repository interface, UseCase)
      ▲  được implement bởi
      │
data (RepositoryImpl, RemoteDataSource/Ktor, DAO/SQLDelight, DTO, Mapper)
```

- **Dependency Rule**: layer ngoài phụ thuộc layer trong; `domain` không phụ thuộc gì ngoài Kotlin + Coroutines.
- **Module**: `feature:*` đứng trên `core:*`; feature không phụ thuộc feature khác.
- **MVI**: state chỉ đổi trong reducer của ViewModel; Composable là hàm thuần của `UiState`.

## Required Reading Order

1. [`README.md`](README.md) — file này.
2. [`AI_AGENT_RULES.md`](AI_AGENT_RULES.md) — luật và workflow bắt buộc cho AI Agent.
3. [`Architecture.md`](Architecture.md) — kiến trúc tổng thể, layer, data flow.
4. [`ProjectStructure.md`](ProjectStructure.md) — tìm code ở đâu.
5. [`StateManagement.md`](StateManagement.md) — hợp đồng MVI.
6. [`CodingStandards.md`](CodingStandards.md) — quy ước code.
7. [`NetworkingGuide.md`](NetworkingGuide.md), [`Api/DeezerApi.md`](Api/DeezerApi.md), [`ErrorHandling.md`](ErrorHandling.md), [`TestingGuide.md`](TestingGuide.md).
8. [`ADR/`](ADR/) — lý do của các quyết định kiến trúc.
9. [`Features/`](Features/) — hiểu feature trước khi sửa.
10. [`Setup/`](Setup/) — dựng môi trường, build, run.

## Important Rules (tóm tắt — chi tiết ở AI_AGENT_RULES.md)

1. **Đọc tài liệu trước khi code.** Tìm implementation tương tự trước khi tạo mới.
2. **Không tạo architecture mới, không tạo state management mới.** Tuân theo Clean Architecture + MVI hiện có.
3. **Không refactor ngoài phạm vi task. Không duplicate implementation.**
4. `domain` là Kotlin thuần. **DTO / Entity không được rò rỉ ra ngoài layer `data`.**
5. UI → ViewModel → UseCase → Repository. Composable không gọi Repository trực tiếp.
6. Luôn xuất **Requirement Analysis → Impact Analysis → Execution Plan** trước khi code, và
   **Files Changed → Summary → Validation → Risks** sau khi code.
