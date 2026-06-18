# ADR-0006: Ktor + kotlinx.serialization cho networking

- **Trạng thái:** Accepted — 2026-06-18

## Context

App streaming là network-centric: catalog, search, playback URL, library — đều qua HTTP, có bearer
token và token refresh. Client phải chạy ở `commonMain` trên Android và iOS.

## Problem

Retrofit/OkHttp chỉ chạy JVM/Android. Cần HTTP client + JSON đa nền tảng, xử lý 401→refresh tập trung.

## Decision

Dùng **Ktor Client** với engine theo nền tảng (OkHttp/Android, Darwin/iOS), plugin
`ContentNegotiation` với **kotlinx.serialization** JSON, `Logging`, `Auth` (bearer + refresh). Một
`HttpClient` factory ở `core:network`. Token đọc từ `SecureStorage` (expect/actual). Chi tiết:
[`../NetworkingGuide.md`](../NetworkingGuide.md).

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Retrofit + OkHttp | JVM/Android only — loại |
| Ktor + Moshi/Gson | Không multiplatform, reflection-based |
| Apollo (GraphQL) | Backend là REST; cân nhắc lại nếu chuyển GraphQL |

## Consequences

- **Tích cực:** một stack network cho cả hai nền tảng; plugin `Auth` xử lý 401→refresh→retry tập trung; serialization compile-time, không reflection; `Logging` hỗ trợ debug.
- **Đánh đổi:** API Ktor ít "batteries-included" hơn Retrofit; khác biệt engine (timeout, cache) cần chú ý theo nền tảng.
- **Khả năng mở rộng:** endpoint mới chỉ là suspend function trên DataSource; concern interceptor/plugin tập trung ở `core:network`; type Ktor không rò khỏi `data`.
