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

## Cập nhật — module `:domain` độc lập (2026-06-23)

Theo yêu cầu của architect, tách **toàn bộ domain** (model + repository interface + use case của
catalog **và** các feature) vào **một module `:domain` thuần Kotlin, độc lập** (chỉ phụ thuộc
`core:common` + coroutines; KHÔNG Ktor/SQLDelight/Compose). Đây là điều chỉnh có chủ đích so với
quyết định gốc "module theo feature, không tách theo layer".

- **Lý do:** đảm bảo domain hoàn toàn độc lập với chi tiết hạ tầng (network/db/UI), có thể tái dùng
  và test thuần; tránh việc một module (vd `catalog`) vừa chứa domain vừa kéo theo deps của data.
- **Hệ quả:** `catalog` trở thành **data-only** (remote/mapper/repositoryImpl/cache/DI) phụ thuộc
  `:domain`; mỗi `feature:*` (presentation/data) phụ thuộc `:domain` thay vì giữ domain riêng;
  `:shared` phụ thuộc `:domain` + `catalog` (data). Package giữ nguyên nên không đổi import.
- **Đánh đổi (đã chấp nhận):** đây là module theo-layer cho tầng domain — coupling tập trung trong
  `:domain` (mọi feature đụng chung 1 module domain), đổi lại domain sạch tuyệt đối và một nguồn duy
  nhất cho hợp đồng nghiệp vụ.
