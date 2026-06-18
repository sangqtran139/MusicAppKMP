# ADR-0001: Ghi lại quyết định kiến trúc bằng ADR

- **Trạng thái:** Accepted — 2026-06-18

## Context

Codebase mới tích lũy nhiều quyết định (thư viện, pattern, cấu trúc) mà cả Developer lẫn AI Agent
phải hiểu và không được vô tình đảo ngược. AI Agent không có ký ức về các thảo luận ngoài repo.

## Problem

Nếu lý do quyết định không được ghi lại ngay trong repo, kiến thức sẽ thất lạc, và người/agent sau
sẽ refactor ngược lại ý định ban đầu.

## Decision

Ghi mọi quyết định kiến trúc quan trọng thành ADR trong `docs/ADR/`, format Context/Problem/Decision/
Alternatives Considered/Consequences. ADR đã Accepted là bất biến; thay đổi bằng ADR mới supersede.

## Alternatives Considered

| Phương án | Vì sao không chọn |
| --- | --- |
| Wiki/Confluence | Trôi khỏi code, không nằm trong PR review, AI Agent khó đọc |
| Không ghi gì | Quyết định bị đảo ngược âm thầm; mất mục tiêu maintainability |

## Consequences

- **Tích cực:** mọi "vì sao" đều grep được, version-controlled, review cùng code; AI Agent đọc ADR để không vi phạm ý định.
- **Đánh đổi:** tốn chút kỷ luật cho mỗi quyết định.
- **Khả năng mở rộng:** index tăng tuyến tính; cơ chế supersede giữ lịch sử trung thực.
