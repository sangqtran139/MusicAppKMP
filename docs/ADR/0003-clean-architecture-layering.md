# ADR-0003: Phân tầng Clean Architecture

- **Trạng thái:** Accepted — 2026-06-18

## Context

App streaming có logic nghiệp vụ không tầm thường (playback, cache offline, entitlement) và backend
sẽ tiến hóa. Các thư viện (Compose, Ktor, SQLDelight) đổi nhanh.

## Problem

Cần cô lập logic nghiệp vụ khỏi UI và hạ tầng để giữ testable và sống sót qua thay đổi thư viện và backend.

## Decision

Áp dụng **Clean Architecture** với 3 layer mỗi feature — `presentation`, `domain`, `data` — và
Dependency Rule một chiều nghiêm ngặt: layer ngoài phụ thuộc layer trong; `domain` chỉ phụ thuộc
Kotlin + Coroutines. Repository **interface** ở `domain`, implementation ở `data`.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| ViewModel → Repository → Retrofit (bỏ domain) | Logic rò vào ViewModel/data class; khó test; UI dính wire format |
| Hexagonal đầy đủ khắp nơi | Quá nặng cho app mobile |

## Consequences

- **Tích cực:** `domain` thuần Kotlin → unit test nhanh, không cần Android/iOS; đổi backend/DB chỉ chạm `data`; AI Agent biết file nằm đâu nên làm feature máy móc.
- **Đánh đổi:** nhiều file (DTO/Entity/Model + Mapper + UseCase) — chấp nhận vì chi phí trả trước, tự động hóa được.
- **Khả năng mở rộng:** UseCase compose được; feature mới tái dùng `core:domain`; phân tầng không đổi khi app lớn lên.
