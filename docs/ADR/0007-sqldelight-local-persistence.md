# ADR-0007: SQLDelight cho local persistence

- **Trạng thái:** Accepted — 2026-06-18

## Context

App offline-first: catalog cache, library người dùng, playback queue, và (sau này) nhạc tải về phải
lưu local và quan sát được dưới dạng `Flow` để UI render theo single source of truth, chạy trên cả
Android và iOS từ `commonMain`.

## Problem

Cần một store quan hệ, đa nền tảng, có `Flow` query và migration rõ ràng.

## Decision

Dùng **SQLDelight 2.x**. Schema và query viết trong file `.sq`; SQLDelight sinh API Kotlin có kiểu.
Driver theo nền tảng qua expect/actual (`AndroidSqliteDriver`, `NativeSqliteDriver`). Query expose
`Flow` qua coroutines extension. DAO/Entity nằm trong `core:database` hoặc `feature/data/local`.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Room (KMP) | Mới trên KMP, annotation processing trên common, story iOS chưa chín bằng |
| Realm Kotlin | Runtime nặng, phụ thuộc vendor, mô hình migration khác |
| Chỉ DataStore | Không quan hệ — sai công cụ cho catalog/library (DataStore chỉ cho prefs) |

## Consequences

- **Tích cực:** SQL có kiểu, check compile-time; `Flow` query làm nền cho offline-first single-source-of-truth; migration tường minh, review được; driver đa nền tảng chín.
- **Đánh đổi:** viết SQL tay; migration thủ công; thêm bước generate.
- **Khả năng mở rộng:** schema quan hệ scale với library lớn; query cô lập trong `data`, không lộ ra `domain`.
