# ADR-0004: MVI cho presentation layer

- **Trạng thái:** Accepted — 2026-06-18

## Context

Cần một cách thống nhất để cấu trúc mọi màn hình sao cho state dự đoán được, dễ test, và mọi người
đóng góp (kể cả AI Agent) đi theo đúng một hình dạng. Compose là declarative, hợp với single source
of truth cho UI state.

## Problem

MVVM tự do (nhiều `StateFlow`/method) dễ tạo tổ hợp state không nhất quán và khó snapshot-test, đặc
biệt với code do agent sinh ra.

## Decision

Dùng **MVI**: mỗi màn hình một `UiState` bất biến, một `sealed interface Intent`, một
`sealed interface Effect`. State chỉ đổi trong reducer của `MviViewModel`. Composable là hàm thuần của
`UiState` + callback `(Intent) -> Unit`. Hợp đồng chi tiết: [`../StateManagement.md`](../StateManagement.md).

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| MVVM (nhiều StateFlow/method) | State rải rác → tổ hợp không hợp lệ, khó test, kém nhất quán cho code agent |
| Compose state hoisting thuần | Business state rò vào UI; không có hợp đồng ViewModel rõ |
| Framework MVI (Orbit, MVIKotlin) | Thêm dependency/khái niệm; một base class ~40 dòng là đủ |

## Consequences

- **Tích cực:** mọi màn hình cùng hình dạng → đọc/mở rộng/review máy móc; `UiState` đơn nhất loại bỏ tổ hợp không hợp lệ; `Effect` tách navigation/snackbar khỏi state; dễ test bằng Turbine.
- **Đánh đổi:** verbose hơn MVVM ad-hoc; đổi một field cũng chạm `UiState` + reducer + composable.
- **Khả năng mở rộng:** pattern đồng nhất qua hàng trăm màn hình; base class là bề mặt chung duy nhất.
