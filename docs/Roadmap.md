# Roadmap — Xây dựng MusicAppKMP

> Plan xây app từ scaffold KMP → MVP, bám [`Setup/BuildGuide.md`](Setup/BuildGuide.md) (Phase 0) và
> công thức 6 bước trong [`Features/README.md`](Features/README.md). Backend: Deezer qua RapidAPI
> ([`Api/DeezerApi.md`](Api/DeezerApi.md)).

## Quyết định nền tảng (đã chốt)

- **Auth MVP = cổng local stub**: UI Welcome/Login theo Figma nhưng chỉ là cổng local, lưu flag đã
  đăng nhập ở DataStore. Không gọi backend. OAuth thật = giai đoạn sau.
- **Build cả Android + iOS song song**: mỗi feature verify trên cả hai nền tảng; tối đa code ở `commonMain`.
- App là **client Deezer + dữ liệu cá nhân lưu local** (liked/playlist/recent/login sống ở thiết bị).

## Hòa giải Figma ↔ Deezer API

| Figma muốn | Deezer thực tế | Quyết định |
| --- | --- | --- |
| Login Google/FB/Apple | Không có tài khoản user | Cổng local stub (flag DataStore) |
| Home: Continue/Top Mixes/Recent | Không có `/home`, không lịch sử | Playlist ID cố định + Recently Played lưu local |
| Explore charts/genres | Không có `/chart`,`/genre` | `/search` + playlist tuyển chọn |
| Player seek + duration đầy đủ | Preview mp3 ~30s | Phát 30s, nhãn "preview" |
| Lyrics | Không có endpoint | Placeholder/ẩn ở MVP |
| Library liked/playlist/album/artist | Không có thư viện user | Lưu local (SQLDelight) |
| Download | Chỉ preview | Bỏ khỏi MVP |

## Trạng thái (cập nhật sau khi build MVP)

| Phase | Trạng thái |
| --- | --- |
| 0 — Nền móng | ✅ Xong (core:common/ui/designsystem, Koin, theme, **Navigation Compose** type-safe — ADR-0008) |
| 1 — Networking + Search | ✅ Xong (Ktor + RapidAPI, catalog, search debounce + index/limit) |
| 2 — Playback | ✅ Xong (ExoPlayer/AVPlayer, now-playing + mini-player, preview 30s) |
| 3 — Detail + cache | ✅ SQLDelight + Album/Artist/Playlist detail + **offline cache** (`networkBoundResource` cho Album; mở rộng artist/playlist sau) |
| 4 — Home + Library | 🟡 Home ✅ (network); Library ✅ (SQLDelight, bền vững qua phiên) |
| 5 — Auth gate | 🟡 Cổng local stub ✅; polish/lyrics còn lại |
| 6 — iOS parity + test + release | 🟡 Compile iOS ✅; chạy test/run device + release còn lại |

> Code hiện nằm dạng **package trong `:shared`** (chưa tách module), build xanh Android + iOS.
> Việc còn lại lớn nhất: **tách module** `core:*`/`feature:*` và mở rộng offline cache cho
> artist/playlist/home. SQLDelight (liked/recent), Navigation Compose, Album/Artist/Playlist
> detail, và `networkBoundResource` (offline-first Album) đã xong.

## Các Phase

Mỗi bước = 1 PR nhỏ; build phải xanh trên cả Android + iOS; thêm test theo `TestingGuide.md`.

### Phase 0 — Nền móng ✅
- [ ] `gradle/libs.versions.toml`: thêm Koin, Ktor, kotlinx.serialization, SQLDelight, DataStore,
      Coil 3, Coroutines, datetime, Kermit, Media3, test libs (verify tương thích Kotlin 2.4.0 / CMP 1.11.1).
- [ ] `RAPIDAPI_KEY` ở `local.properties` + wiring Gradle → build config → `ApiConfig` (Koin).
- [ ] `core:common`: `AppResult`, `AppError`, `DispatcherProvider`, Kermit logger.
- [ ] `core:ui`: `MviViewModel`, `UiState`/`Intent`/`Effect`.
- [ ] `core:designsystem`: theme **dark + accent teal** (trích từ Figma), typography, spacing, `AppAsyncImage` (Coil).
- [ ] Khởi tạo Koin (Android `Application` + bootstrap iOS).
- [ ] NavHost + **bottom nav** Home/Explore/Library (3 màn rỗng đã theme).
- **Done khi:** app chạy 2 nền tảng, 3 tab điều hướng được, Koin `verify()` xanh.

### Phase 1 — Networking + Search (lát cắt dọc) ✅
- [ ] `core:network`: `HttpClient` factory (header RapidAPI), JSON, Logging (ẩn key), map lỗi + body-error Deezer.
- [ ] `CatalogApi` + DTO (`TrackDto`/`AlbumDto`/`ArtistDto`/`PlaylistDto`/`SearchResultDto`) + mapper `toDomain()`.
- [ ] `feature:search`: domain → data → presentation; debounce 300ms; phân trang `index/limit`.
- **Done khi:** gõ tìm ra track thật, có empty/loading/error state.

### Phase 2 — Playback ✅
- [ ] `core:playback`: `AudioPlayer` + `AudioPlayerFactory` expect/actual (ExoPlayer / AVPlayer), `PlaybackState`.
- [ ] `feature:player`: phát **preview 30s**, now-playing theo Figma `song-player`.
- [ ] **Mini-player** overlay ở `:shared` (cùng `PlaybackState`).
- **Done khi:** tap kết quả → phát 30s, mini-player + now-playing đồng bộ trên cả hai nền tảng.

### Phase 3 — Detail + cache offline 🟡
- [x] `core/database` (SQLDelight + `MusicDatabase.sq`; driver expect/actual qua `platformModule`).
- [x] `core/data` (`networkBoundResource`) + `Resource` (core/common) — offline-first single-source-of-truth.
- [x] Offline cache **Album** (cache SQLDelight + refresh network; reopen album offline được). Còn: mở rộng artist/playlist/search.
- [x] **Navigation Compose** type-safe (NavHost `:shared`, route `@Serializable`, ADR-0008).
- [x] Màn **Album detail** (`/album`, đọc `tracks.data`; entry: tap cover ở Home/Explore/Library).
- [x] Màn **Artist detail** (`/artist`, metadata-only — proxy không có top-tracks; entry: tap tên artist ở list rows).
- [x] Màn **Playlist detail** (`/playlist`, đọc `tracks.data`; entry: hàng "Featured playlists" ở Home).
- [x] Lưu **Recently Played** local (SQLDelight, cap 20, bền vững qua phiên).

### Phase 4 — Home + Library 🟡
- [x] `feature:home`: **Featured playlists** (ID seed cố định) + sections theo chủ đề. Còn lại: Recently Played trên Home + curate playlist ID thật.
- [x] `feature:library`: liked songs (SQLDelight, bền vững). Còn lại: playlist tự tạo + chips Playlists/Artists/Albums.

### Phase 5 — Auth gate + Onboarding + polish 🟡
- [ ] Welcome + Login UI (Figma) làm cổng local (flag DataStore) + điều hướng gate.
- [ ] Polish: empty/loading/error states, lyrics placeholder, chi tiết theo Figma.

### Phase 6 — iOS parity + test + release 🟡
- [ ] Rà parity iOS (AVPlayer, framework), bù test (UseCase/Mapper/ViewModel + Turbine, Koin `verify()`).
- [ ] Release prep: minify/R8, ký, background audio mode.

## Tham chiếu
[`Architecture.md`](Architecture.md) · [`ProjectStructure.md`](ProjectStructure.md) ·
[`StateManagement.md`](StateManagement.md) · [`NetworkingGuide.md`](NetworkingGuide.md) ·
[`Api/DeezerApi.md`](Api/DeezerApi.md) · [`Features/`](Features/) · [`ADR/`](ADR/)
