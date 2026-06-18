# Architecture Decision Records (ADR)

Mỗi ADR ghi lại **một** quyết định kiến trúc quan trọng để AI Agent và Developer hiểu **lý do**, và
không vô tình đảo ngược. ADR đã `Accepted` thì bất biến; muốn thay đổi thì viết ADR mới `Supersedes`
ADR cũ.

Format mỗi ADR: **Context · Problem · Decision · Alternatives Considered · Consequences**.

## Index

| # | Tiêu đề | Trạng thái |
| --- | --- | --- |
| [0001](0001-record-architecture-decisions.md) | Ghi lại quyết định kiến trúc bằng ADR | Accepted |
| [0002](0002-kotlin-multiplatform-compose.md) | Kotlin Multiplatform + Compose Multiplatform | Accepted |
| [0003](0003-clean-architecture-layering.md) | Phân tầng Clean Architecture | Accepted |
| [0004](0004-mvi-presentation-pattern.md) | MVI cho presentation layer | Accepted |
| [0005](0005-koin-dependency-injection.md) | Koin cho Dependency Injection | Accepted |
| [0006](0006-ktor-networking.md) | Ktor + kotlinx.serialization cho networking | Accepted |
| [0007](0007-sqldelight-local-persistence.md) | SQLDelight cho local persistence | Accepted |
| [0008](0008-navigation.md) | Navigation Compose Multiplatform (type-safe) | Accepted |
| [0009](0009-feature-modularization.md) | Modularization theo feature | Accepted |
| [0010](0010-audio-playback-expect-actual.md) | Playback native qua expect/actual | Accepted |
| [0011](0011-coil-image-loading.md) | Coil 3 cho image loading | Accepted |

## Thêm ADR mới

1. Tạo `NNNN-tieu-de-ngan.md` (số kế tiếp, đủ 4 chữ số).
2. Điền đủ 5 mục. Một ADR = một quyết định.
3. Thêm dòng vào bảng Index.
