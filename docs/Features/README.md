# Features

Mỗi feature là một `:feature:*` module, cấu trúc 3 layer (`domain`/`data`/`presentation`) + `di`.
AI Agent phải đọc tài liệu feature tương ứng **trước khi** sửa code feature đó.

## Danh sách feature (MVP)

| Feature | Module | Mô tả ngắn | Tài liệu |
| --- | --- | --- | --- |
| Auth | `:feature:auth` | Đăng nhập, quản lý session/token, gate toàn app | [Auth.md](Auth.md) |
| Home | `:feature:home` | Trang chủ / browse nội dung được tuyển chọn | [Home.md](Home.md) |
| Search | `:feature:search` | Tìm kiếm track/album/artist trong catalog | [Search.md](Search.md) |
| Player | `:feature:player` | Now-playing + mini-player, điều khiển playback | [Player.md](Player.md) |
| Library | `:feature:library` | Bài đã lưu, playlist, downloads (offline-first) | [Library.md](Library.md) |

Giai đoạn sau: Playlist detail, Artist/Album detail, Queue management, Downloads — bổ sung tài liệu khi triển khai.

## Cách thêm feature mới (công thức bắt buộc)

Theo đúng thứ tự. Đây là pattern duy nhất — copy shape của feature gần nhất, **không** tạo cấu trúc khác.

1. **domain** (`feature/<x>/domain`): Model (nếu cần), `XxxRepository` (interface), UseCase
   (`operator fun invoke`, trả `AppResult<T>` hoặc `Flow<AppResult<T>>`). Không import framework.
2. **data** (`feature/<x>/data`): `XxxDto` (`@Serializable`), `XxxApi`/RemoteDataSource (Ktor),
   `XxxDao` (SQLDelight), `XxxMappers.kt`, `XxxRepositoryImpl`. DTO/Entity không ra khỏi đây.
3. **presentation** (`feature/<x>/presentation`): `XxxContract.kt` (`UiState`/`Intent`/`Effect`),
   `XxxViewModel` (extends `MviViewModel`), `XxxScreen` (stateful) + `XxxContent` (stateless, `@Preview`).
4. **di** (`feature/<x>/di/XxxModule.kt`): khai báo Koin module, **đăng ký trong `:shared`**.
5. **navigation**: route `@Serializable` + `NavGraphBuilder.xxxGraph(...)`, wire vào NavHost ở `:shared`.
   Không để feature phụ thuộc feature.
6. **test**: UseCase + Mapper + ViewModel (Turbine); thêm module vào Koin `verify()` test.

Tham chiếu: [`../Architecture.md`](../Architecture.md), [`../StateManagement.md`](../StateManagement.md),
[`../CodingStandards.md`](../CodingStandards.md).

## Template tài liệu cho feature mới

Mỗi file feature phải có các mục: **Purpose · Business Flow · User Flow · Screens · Navigation Flow ·
State Flow · API Used · Related Modules · Known Issues**.
