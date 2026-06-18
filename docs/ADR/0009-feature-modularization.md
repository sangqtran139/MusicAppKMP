# ADR-0009: Modularization theo feature

- **Trạng thái:** Accepted — 2026-06-18

## Context

Scaffold chỉ có một module `:shared`. Khi feature và team lớn lên, module nguyên khối làm build chậm,
không enforce được Dependency Rule, và gây va chạm merge.

## Problem

Cần ranh giới enforce ở compile-time và build incremental nhanh khi codebase mở rộng.

## Decision

Tách thành **`:core:*`** (năng lực dùng chung) và **`:feature:*`** (mỗi mảng sản phẩm) theo
[`../ProjectStructure.md`](../ProjectStructure.md). Feature phụ thuộc core, không phụ thuộc nhau;
`:shared` gom tất cả và build iOS framework. Cấu hình KMP chung tập trung ở convention plugins trong
`build-logic`.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Một `:shared`, chỉ tách package | Dependency Rule không enforce; recompile toàn bộ; va chạm merge |
| Module theo layer (`:domain`, `:data`, `:ui`) | Mọi feature chạm mọi module layer → coupling, kém song song |
| Tách `api`/`impl` mỗi feature ngay | Quá sớm, gấp đôi số module — hoãn tới khi cần |

## Consequences

- **Tích cực:** build enforce Dependency Rule (feature không import nội bộ feature khác); incremental build chỉ build module đổi; team/agent làm song song ít va chạm; convention plugin bỏ boilerplate.
- **Đánh đổi:** nhiều file `build.gradle.kts`; clean build chậm hơn; setup convention plugin tốn công đầu; cần cẩn thận export iOS framework.
- **Khả năng mở rộng:** tuyến tính — mỗi feature một module; feature lớn có thể tách `api`/`impl` sau mà không đổi mô hình.
