# ADR-0005: Koin cho Dependency Injection

- **Trạng thái:** Accepted — 2026-06-18

## Context

Clean Architecture + modularization tạo nhiều class nhỏ cần wire xuyên module (UseCase, Repository,
DataSource, ViewModel). Cần một giải pháp DI chạy ở `commonMain` cho cả Android và iOS, tích hợp
Compose và KMP ViewModel.

## Problem

Dagger/Hilt chỉ chạy JVM/Android, không wire được iOS. DI thủ công bùng nổ boilerplate khi số module tăng.

## Decision

Dùng **Koin** (`koin-core` định nghĩa module, `koin-compose` cho `koinViewModel()`). Mỗi module
expose một `*Module`; `:shared` gom lại và khởi tạo Koin lúc app start (Android `Application`, iOS qua
bootstrap Swift).

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Dagger/Hilt | Không multiplatform — loại |
| kotlin-inject | Compile-time, KMP được, nhưng setup nặng hơn, KSP mỗi module; cân nhắc lại nếu cần |
| DI thủ công | Boilerplate không scale |

## Consequences

- **Tích cực:** một cơ chế DI cho cả hai nền tảng; `koinViewModel()` tích hợp Compose + lifecycle; module Koin ánh xạ module Gradle; dễ thay fake trong test.
- **Đánh đổi:** resolve runtime → lỗi wiring lộ lúc start, không phải compile-time. Giảm thiểu bằng test `koin-test` `verify()` trong CI.
- **Khả năng mở rộng:** thêm feature = thêm một `*Module` + đăng ký; graph khai báo, gọn.
