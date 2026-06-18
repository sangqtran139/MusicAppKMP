# ADR-0002: Kotlin Multiplatform + Compose Multiplatform

- **Trạng thái:** Accepted — 2026-06-18

## Context

Sản phẩm phải chạy trên Android và iOS. Repo được scaffold bằng KMP wizard, đã có `androidApp` +
`iosApp` + `shared` dùng Compose Multiplatform 1.11.1 trên Kotlin 2.4.0.

## Problem

Cần quyết định: chia sẻ cả UI hay chỉ chia sẻ logic và viết UI native hai lần.

## Decision

Cam kết **Kotlin Multiplatform cho toàn bộ logic** và **Compose Multiplatform cho UI dùng chung**.
Hai nền tảng render cùng một Compose UI từ `commonMain`; entry point nền tảng (`Activity`,
`ComposeUIViewController`) giữ mỏng.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| KMP logic + native UI (SwiftUI/Compose-Android) | UI viết hai lần — chi phí lớn nhất với app nhạc nhiều màn hình dùng chung |
| Flutter / React Native | Rời hệ sinh thái Kotlin/JVM, tích hợp audio native yếu hơn, đổi ngôn ngữ |
| Native hoàn toàn (2 app) | Hai codebase đầy đủ — đi ngược mục tiêu dự án |

## Consequences

- **Tích cực:** một UI + một bộ logic; >90% code dùng chung; sửa bug một chỗ.
- **Đánh đổi:** Compose trên iOS còn non hơn UIKit/SwiftUI; Material3 đang ở alpha; cần chú ý vài chi tiết (text input, accessibility).
- **Khả năng mở rộng:** thêm Desktop/Web sau này là cộng thêm source set + `actual`, không phải viết lại.
