# ADR-0008: Navigation Compose Multiplatform (type-safe)

- **Trạng thái:** Accepted — 2026-06-18

## Context

Cần navigation chạy trong Compose UI dùng chung Android/iOS, hỗ trợ bottom-nav + nested graph
(Home/Search/Library + Player toàn màn hình), và cho feature module tự khai báo destination mà không
phụ thuộc lẫn nhau.

## Problem

Nếu feature phụ thuộc feature để điều hướng, Dependency Rule và tính độc lập module bị phá vỡ.

## Decision

Dùng **Jetpack Navigation Compose (Multiplatform)** với route **type-safe** (`@Serializable`). Mỗi
feature expose route + extension `NavGraphBuilder.xxxGraph(...)`; **NavHost và lắp ráp graph ở
`:shared`**. Điều hướng giữa feature dùng route/callback có kiểu.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Decompose | Mạnh nhưng mental model khác, nhiều boilerplate hơn |
| Voyager | Third-party, không chắc đồng hành dài hạn với nav chính thức |
| Sealed-class navigator tự viết | Phải tự làm back stack, deep link, animation |

## Consequences

- **Tích cực:** arg có kiểu (check compile-time qua kotlinx.serialization); hỗ trợ chính thức dài hạn; feature decoupled qua extension; deep link + transition có sẵn.
- **Đánh đổi:** nav Compose Multiplatform non hơn bản Android-only; vài chi tiết animation/predictive-back phụ thuộc nền tảng.
- **Khả năng mở rộng:** thêm destination là cộng dồn; nested graph giữ app lớn gọn; `:shared` là nơi duy nhất biết toàn bộ bản đồ.
