# ADR-0011: Coil 3 cho image loading

- **Trạng thái:** Accepted — 2026-06-18

## Context

App nhạc nhiều ảnh: artwork album/playlist/artist trong grid và màn hình now-playing. Cần load ảnh
async có cache memory + disk, placeholder, tích hợp Compose, chạy trong UI dùng chung Android/iOS.

## Problem

Cần một image loader đa nền tảng, Compose-first, tái dùng được HTTP stack hiện có.

## Decision

Dùng **Coil 3** (KMP-native, Compose-first) với fetcher Ktor (`coil-network-ktor3`) để tái dùng HTTP
stack. Một wrapper mỏng `AppAsyncImage` trong `:core:designsystem` chuẩn hóa placeholder, crossfade,
ảnh lỗi.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Kamel | Hệ sinh thái nhỏ hơn, ít active hơn |
| Glide/SDWebImage qua expect/actual | Hai implementation — phá UI dùng chung |

## Consequences

- **Tích cực:** một API ảnh trong Compose dùng chung; tái dùng Ktor (auth/cache chung); cache memory/disk sẵn; placeholder/error gọn qua một wrapper.
- **Đánh đổi:** Coil 3 KMP còn mới; tuning disk-cache iOS cần chú ý.
- **Khả năng mở rộng:** bọc trong `AppAsyncImage` nên đổi config/thư viện sau này chỉ một file.
