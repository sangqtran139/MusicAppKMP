# AI_AGENT_RULES

Tài liệu này là **luật bắt buộc** cho mọi AI Agent (Claude Code, Cursor, Codex, Gemini CLI) và
Developer khi làm việc trên MusicAppKMP. Đọc trước khi viết bất kỳ dòng code nào.

---

## Nguyên tắc cốt lõi

AI Agent **phải**:

- Đọc tài liệu trước khi code (bắt đầu từ [`README.md`](README.md)).
- Tìm implementation tương tự trong codebase trước khi tạo mới.
- Ưu tiên **reuse** code hiện có.
- Luôn phân tích ảnh hưởng trước khi thay đổi.

AI Agent **không được**:

- Tạo architecture mới.
- Tạo state management mới (chỉ dùng MVI hiện có — xem [`StateManagement.md`](StateManagement.md)).
- Refactor ngoài phạm vi task.
- Duplicate implementation.
- Để DTO / Entity rò rỉ ra ngoài layer `data`.
- Gọi Repository / DataSource trực tiếp từ Composable.
- Thêm thư viện mới khi đã có thư viện tương đương (xem Tech Stack trong README).

---

## Output bắt buộc

### Trước khi implement — phải xuất 3 mục

#### Requirement Analysis
- Task yêu cầu gì? Input/output mong đợi? Tiêu chí "done".
- Thuộc feature nào (xem [`Features/`](Features/))? Là feature mới hay sửa feature cũ?

#### Impact Analysis
- Những layer/module/file nào bị ảnh hưởng (`presentation` / `domain` / `data`)?
- Có ảnh hưởng `core:*` dùng chung không? Có đổi public API của module không?
- Rủi ro va chạm với code hiện có; có DI binding / navigation route nào phải cập nhật không?
- **Khu vực KHÔNG được đụng** liên quan tới task này (xem mục "Không nên sửa" bên dưới).

#### Execution Plan
- Các bước theo thứ tự `domain → data → presentation → DI → navigation → test`.
- File sẽ tạo/sửa, kèm lý do reuse (đã tìm thấy implementation tương tự nào).

### Sau khi implement — phải xuất 4 mục

#### Files Changed
- Liệt kê từng file tạo/sửa/xóa + mô tả ngắn thay đổi.

#### Summary
- Tóm tắt thay đổi và cách nó tuân theo kiến trúc hiện tại.

#### Validation
- Đã build/test gì: `./gradlew build`, test nào chạy, kết quả. Nếu chưa chạy được thì nói rõ.

#### Risks
- Rủi ro còn lại, edge case chưa xử lý, technical debt, việc cần theo dõi.

---

## Workflow khi nhận task

1. Đọc [`README.md`](README.md) → [`Architecture.md`](Architecture.md) → tài liệu feature liên quan trong [`Features/`](Features/).
2. Tìm trong codebase implementation/feature tương tự (xem [`ProjectStructure.md`](ProjectStructure.md) để biết tìm ở đâu).
3. Xuất **Requirement Analysis → Impact Analysis → Execution Plan**.
4. Chỉ implement sau khi plan rõ ràng và nằm trong phạm vi task.
5. Xuất **Files Changed → Summary → Validation → Risks**.

## Workflow khi thêm feature

Theo đúng công thức trong [`Features/README.md`](Features/README.md) (mục "Cách thêm feature mới"):

1. **domain**: Model (nếu cần), Repository interface, UseCase (`operator fun invoke`, trả `AppResult`).
2. **data**: DTO (`@Serializable`, suffix `Dto`), RemoteDataSource (Ktor), DAO (SQLDelight), Mapper, RepositoryImpl.
3. **presentation**: `XxxContract.kt` (`UiState`/`Intent`/`Effect`), `XxxViewModel` (extends `MviViewModel`), `XxxScreen` (stateful) + `XxxContent` (stateless).
4. **DI**: tạo `xxxModule` (Koin) và **đăng ký trong `:shared`**.
5. **navigation**: route `@Serializable` + `NavGraphBuilder.xxxGraph()`, wire trong `:shared`.
6. **test**: UseCase + Mapper + ViewModel (Turbine).

Quy tắc: **không tạo cấu trúc khác** với feature đã có. Copy shape của feature gần nhất.

## Workflow khi fix bug

1. Tái hiện bug và xác định **layer** chứa nguyên nhân:
   - Sai dữ liệu hiển thị / sai state → bắt đầu ở `presentation` (ViewModel reducer) rồi lần xuống.
   - Sai dữ liệu trả về / parse / cache → `data` (RemoteDataSource, Mapper, DAO).
   - Sai logic nghiệp vụ → `domain` (UseCase).
   - Crash playback / quyền / nền tảng → `core:playback` hoặc `expect/actual` tương ứng.
2. Đọc tài liệu feature trong [`Features/`](Features/), mục **Known Issues**.
3. Sửa **đúng tại nguồn gốc**, không vá ở layer trên. Không mở rộng phạm vi sang refactor.
4. Thêm test tái hiện bug để chống tái diễn (xem [`TestingGuide.md`](TestingGuide.md)).

## Workflow khi review code

- Kiểm Dependency Rule: `domain` không import framework; DTO/Entity không rò rỉ.
- Kiểm MVI: state chỉ đổi trong reducer; Composable thuần; `Effect` cho one-shot.
- Kiểm error: `data` map exception → `AppError`; layer trên không `try/catch` thư viện.
- Kiểm DI: có `*Module` và đã đăng ký ở `:shared`; graph `verify()` được.
- Kiểm reuse: không duplicate UseCase/Mapper/Component đã tồn tại.
- Kiểm test + naming theo [`CodingStandards.md`](CodingStandards.md).

---

## Không nên sửa (Do-Not-Touch zones)

- **Cấu trúc layer và Dependency Rule** trong `Architecture.md` — không đảo chiều phụ thuộc.
- **`MviViewModel` base và hợp đồng `UiState`/`Intent`/`Effect`** — không thay bằng pattern khác.
- **`AppResult` / `AppError`** trong `core:common` — không thay bằng exception xuyên layer.
- **`HttpClient` factory, Auth/refresh** trong `core:network` — chỉ sửa khi task nói rõ về networking.
- **`AudioPlayer` interface** trong `core:playback` — mở rộng cẩn thận, vì có 2 `actual`.
- **Gradle build / `libs.versions.toml`** — chỉ đổi khi task là setup/build; nêu rõ trong Impact Analysis.
- **Generated code** (SQLDelight, serialization) — không sửa tay.

## Khi gặp quyết định kiến trúc mới

Không tự ý đặt ra pattern mới. Nếu task buộc phải có quyết định kiến trúc chưa có tiền lệ:
viết một ADR mới ([`ADR/`](ADR/), theo format Context/Problem/Decision/Alternatives Considered/
Consequences) và nêu trong output, thay vì âm thầm lệch khỏi kiến trúc.
