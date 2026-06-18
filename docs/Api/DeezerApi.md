# Deezer API (qua RapidAPI) — Tài liệu tham chiếu

> Backend nội dung (catalog) của MusicAppKMP là **Deezer**, truy cập qua proxy
> **RapidAPI** `deezerdevs-deezer`. Tài liệu này mô tả **chính xác** những gì proxy này
> thực sự trả về (đã verify bằng request thật), không phải toàn bộ Deezer API gốc.
>
> Phần networking (Ktor client, header, error) xem [`../NetworkingGuide.md`](../NetworkingGuide.md).
> Map error sang `AppError` xem [`../ErrorHandling.md`](../ErrorHandling.md).

## 1. Tổng quan kết nối / Connection

| Mục | Giá trị |
| --- | --- |
| Base URL | `https://deezerdevs-deezer.p.rapidapi.com` |
| Auth | Static header (KHÔNG OAuth, KHÔNG login, KHÔNG refresh token) |
| Header bắt buộc | `X-RapidAPI-Key: <RAPIDAPI_KEY>` |
| Header bắt buộc | `X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com` |
| Định dạng | JSON (UTF-8) |
| Marketplace | https://rapidapi.com/deezerdevs/api/deezer-1 |

> ⚠️ **Bảo mật:** `RAPIDAPI_KEY` KHÔNG hardcode, KHÔNG commit, KHÔNG log. Lưu ở
> `local.properties` (đã `.gitignore`) và inject qua build config. Chi tiết:
> [`../NetworkingGuide.md`](../NetworkingGuide.md) mục *API Key handling*.
>
> ℹ️ Deezer **không có khái niệm tài khoản người dùng** ở proxy này — mọi request đều
> ẩn danh. Vì vậy app **không** có login/logout/refresh token cho catalog. Nếu sau này cần
> auth (playlist cá nhân, đồng bộ thư viện) thì phải là backend riêng — ngoài phạm vi tài liệu này.

## 2. Danh sách Endpoint khả dụng / Available endpoints

Proxy `deezerdevs-deezer` **chỉ** mở **5 endpoint** dưới đây. Các endpoint Deezer gốc khác
(`/chart`, `/genre`, `/radio`, `/editorial`, `/artist/{id}/top`, `/artist/{id}/albums`,
`/album/{id}/tracks`, …) đều trả `{"message":"Endpoint '...' does not exist"}` — **không dùng được**.

| Method & Path | Mô tả | Trả về (object) |
| --- | --- | --- |
| `GET /search?q={query}` | Tìm track theo từ khóa | [`SearchResult`](#31-searchresult) (list [`TrackLite`](#42-tracklite)) |
| `GET /track/{id}` | Chi tiết 1 track | [`Track`](#41-track) (đầy đủ) |
| `GET /album/{id}` | Chi tiết album + tracklist | [`Album`](#43-album) |
| `GET /artist/{id}` | Chi tiết nghệ sĩ | [`Artist`](#44-artist) |
| `GET /playlist/{id}` | Chi tiết playlist + tracklist | [`Playlist`](#45-playlist) |

> 💡 Vì không có `/album/{id}/tracks` và `/artist/{id}/top`, muốn lấy **track của album**
> thì đọc field `tracks.data` trong response của `GET /album/{id}`; muốn lấy **track của
> playlist** thì đọc `tracks.data` trong `GET /playlist/{id}`. Track-list của artist
> hiện **không** lấy được qua proxy này.

### 2.1. Phân trang / Pagination (chỉ áp dụng cho `/search`)

| Param | Kiểu | Mặc định | Ý nghĩa |
| --- | --- | --- | --- |
| `q` | string | — (bắt buộc) | Từ khóa tìm kiếm (plain text). |
| `index` | int | `0` | Vị trí bắt đầu (offset). |
| `limit` | int | `25` | Số item mỗi trang. |

- Trang kế tiếp: tăng `index` thêm `limit` (vd `index=0` → `index=25` → `index=50`).
- Response có field `next` nhưng URL trỏ về `api.deezer.com` (host gốc) — **không gọi trực
  tiếp qua proxy được**. Dùng `next` chỉ để biết "còn trang sau hay không", còn phân trang
  thực tế thì tự tăng `index`.
- `total` = tổng số kết quả ước tính.

### 2.2. Advanced search — ⚠️ KHÔNG hỗ trợ

Cú pháp filter của Deezer gốc (`artist:"..."`, `album:"..."`, `track:"..."`, `dur_min`,
`bpm_min`…) khi gọi qua proxy này trả về `total: 0` (đã test). **Chỉ dùng plain query** trong `q`.

## 3. Response bao bọc / Wrapper objects

### 3.1. `SearchResult`

```json
{ "data": [ TrackLite, ... ], "total": 208, "next": "https://api.deezer.com/search?..." }
```

| Field | Kiểu | Ghi chú |
| --- | --- | --- |
| `data` | `TrackLite[]` | Danh sách kết quả (xem [`TrackLite`](#42-tracklite)). |
| `total` | int | Tổng số kết quả. |
| `next` | string? | URL trang sau (host gốc — chỉ để kiểm tra còn dữ liệu). Vắng mặt nếu hết. |

> `tracks` trong [`Album`](#43-album) và [`Playlist`](#45-playlist) cũng là wrapper dạng
> `{ "data": [...], "checksum"? , "total"? }`.

## 4. Mô hình dữ liệu / Data models

> **Lưu ý map DTO:** đặt tất cả DTO ở layer `data`, không để rò rỉ ra `domain`
> (xem [`../CodingStandards.md`](../CodingStandards.md) & Hard rule #4). Field `type`
> (`"track"`, `"album"`, `"artist"`, `"playlist"`, `"genre"`) là discriminator của Deezer —
> tiện cho `@SerialName`/polymorphic nếu cần.

### 4.1. `Track` (đầy đủ — từ `GET /track/{id}`)

| Field | Kiểu | Ý nghĩa |
| --- | --- | --- |
| `id` | long | ID track. |
| `readable` | bool | Có thể phát ở region hiện tại. |
| `title` | string | Tên đầy đủ. |
| `title_short` | string | Tên ngắn (bỏ phần version). |
| `title_version` | string | Phần version (vd "Radio Edit"), có thể rỗng. |
| `isrc` | string | Mã ISRC. |
| `link` | string | URL Deezer của track. |
| `share` | string | URL chia sẻ. |
| `duration` | int | Thời lượng **giây**. |
| `track_position` | int | Thứ tự trong album. |
| `disk_number` | int | Số đĩa. |
| `rank` | int | Độ phổ biến (càng cao càng hot). |
| `release_date` | string | `YYYY-MM-DD`. |
| `explicit_lyrics` | bool | Lời tục hay không. |
| `explicit_content_lyrics` | int | Enum explicit (xem [§4.7](#47-enum-explicit_content)). |
| `explicit_content_cover` | int | Enum explicit cho ảnh bìa. |
| `preview` | string | **URL mp3 ~30s** để nghe thử ⭐ (xem [§5](#5-preview--phát-thử)). |
| `bpm` | float | Nhịp (có thể `0` nếu thiếu data). |
| `gain` | float | Mức gain (dB). |
| `available_countries` | string[] | Danh sách mã quốc gia ISO. |
| `contributors` | [`Contributor`](#46-contributor)[] | Nghệ sĩ đóng góp + `role`. |
| `md5_image` | string | Hash ảnh. |
| `track_token` | string | Token nội bộ Deezer (không dùng cho playback ở proxy này). |
| `artist` | [`ArtistLite`](#44-artist) | Nghệ sĩ chính (bản rút gọn). |
| `album` | [`AlbumLite`](#43-album) | Album chứa track (bản rút gọn). |
| `type` | string | `"track"`. |

### 4.2. `TrackLite` (rút gọn — trong `SearchResult.data`)

Giống `Track` nhưng **không có**: `share`, `track_position`, `disk_number`, `release_date`,
`bpm`, `gain`, `available_countries`, `contributors`, `track_token`.

| Field | Kiểu |
| --- | --- |
| `id`, `readable`, `title`, `title_short`, `title_version`, `isrc` | (như trên) |
| `link`, `duration`, `rank` | (như trên) |
| `explicit_lyrics`, `explicit_content_lyrics`, `explicit_content_cover` | (như trên) |
| `preview`, `md5_image` | (như trên) |
| `artist` | [`ArtistLite`](#44-artist) |
| `album` | [`AlbumLite`](#43-album) |
| `type` | `"track"` |

> Track trong `playlist.tracks.data` giống `TrackLite` nhưng có thêm `isrc` và `time_add` (Unix timestamp lúc thêm vào playlist).
> Track trong `album.tracks.data` giống `TrackLite` nhưng **không** có `isrc`.

### 4.3. `Album`

**`AlbumLite`** (nhúng trong `Track`): `id`, `title`, `link`, `cover`, `cover_small`,
`cover_medium`, `cover_big`, `cover_xl`, `md5_image`, `release_date`, `tracklist`, `type`.

**`Album`** (đầy đủ — từ `GET /album/{id}`):

| Field | Kiểu | Ý nghĩa |
| --- | --- | --- |
| `id` | long | ID album. |
| `title` | string | Tên album. |
| `upc` | string | Mã UPC. |
| `link`, `share` | string | URL Deezer / chia sẻ. |
| `cover` | string | URL ảnh bìa (API, redirect). |
| `cover_small` / `cover_medium` / `cover_big` / `cover_xl` | string | Ảnh 56 / 250 / 500 / 1000 px. |
| `md5_image` | string | Hash ảnh. |
| `genre_id` | int | ID genre chính (`-1` nếu không có). |
| `genres` | `{ data: Genre[] }` | Danh sách genre (xem [`Genre`](#48-genre)). |
| `label` | string | Hãng phát hành. |
| `nb_tracks` | int | Số track. |
| `duration` | int | Tổng thời lượng (giây). |
| `fans` | int | Số fan. |
| `release_date` | string | `YYYY-MM-DD`. |
| `record_type` | string | `"album"`, `"single"`, `"ep"`, `"compile"`… |
| `available` | bool | Có khả dụng không. |
| `tracklist` | string | URL tracklist (host gốc — không qua proxy). |
| `explicit_lyrics` | bool | |
| `explicit_content_lyrics` / `explicit_content_cover` | int | [Enum](#47-enum-explicit_content). |
| `contributors` | [`Contributor`](#46-contributor)[] | |
| `artist` | `ArtistLite` (id, name, picture_*, tracklist, type) | Nghệ sĩ chính. |
| `tracks` | `{ data: TrackLite[] }` | **Tracklist nhúng** (dùng thay cho `/album/{id}/tracks`). |
| `type` | string | `"album"`. |

### 4.4. `Artist`

**`ArtistLite`** (nhúng trong `Track`): `id`, `name`, `link`, `share`, `picture`,
`picture_small`, `picture_medium`, `picture_big`, `picture_xl`, `radio`, `tracklist`, `type`.

**`Artist`** (đầy đủ — từ `GET /artist/{id}`):

| Field | Kiểu | Ý nghĩa |
| --- | --- | --- |
| `id` | long | ID nghệ sĩ. |
| `name` | string | Tên. |
| `link`, `share` | string | URL Deezer / chia sẻ. |
| `picture` | string | URL ảnh (API, redirect). |
| `picture_small` / `picture_medium` / `picture_big` / `picture_xl` | string | 56 / 250 / 500 / 1000 px. |
| `nb_album` | int | Số album. |
| `nb_fan` | int | Số fan. |
| `radio` | bool | Có radio không. |
| `tracklist` | string | URL top tracks (host gốc — **không** gọi được qua proxy). |
| `type` | string | `"artist"`. |

### 4.5. `Playlist` (từ `GET /playlist/{id}`)

| Field | Kiểu | Ý nghĩa |
| --- | --- | --- |
| `id` | long | ID playlist. |
| `title` | string | Tên. |
| `description` | string | Mô tả. |
| `duration` | int | Tổng thời lượng (giây). |
| `public` | bool | Công khai. |
| `is_loved_track` | bool | Có phải playlist "Favourite" không. |
| `collaborative` | bool | Cho cộng tác. |
| `nb_tracks` | int | Số track. |
| `fans` | int | Số fan. |
| `link`, `share` | string | URL Deezer / chia sẻ. |
| `picture` | string | URL ảnh (API, redirect). |
| `picture_small` / `picture_medium` / `picture_big` / `picture_xl` | string | 56 / 250 / 500 / 1000 px. |
| `picture_type` | string | Loại ảnh (vd `"playlist"`). |
| `checksum` | string | Checksum tracklist. |
| `tracklist` | string | URL tracklist (host gốc). |
| `creation_date` / `add_date` / `mod_date` | string | Thời điểm tạo / thêm / sửa. |
| `md5_image` | string | Hash ảnh. |
| `creator` | `{ id, name, tracklist, type:"user" }` | Người tạo. |
| `tracks` | `{ data: TrackLite[] (có thêm time_add), checksum }` | **Tracklist nhúng.** |
| `type` | string | `"playlist"`. |

### 4.6. `Contributor`

Như `ArtistLite` (`id`, `name`, `link`, `share`, `picture`, `picture_small/medium/big/xl`,
`radio`, `tracklist`, `type`) **+ thêm** `role` (string, vd `"Main"`, `"Featured"`).

### 4.7. Enum `explicit_content_*`

Các field `explicit_content_lyrics` / `explicit_content_cover` là **int**:

| Giá trị | Ý nghĩa |
| --- | --- |
| `0` | Not Explicit |
| `1` | Explicit |
| `2` | Unknown |
| `3` | Edited |
| `4` | Partially Explicit (chỉ "lyrics" ở album) |
| `5` | Partially Unknown (chỉ "lyrics" ở album) |
| `6` | No Advice Available |
| `7` | Partially No Advice Available (chỉ "lyrics" ở album) |

### 4.8. `Genre` (trong `album.genres.data`)

| Field | Kiểu |
| --- | --- |
| `id` | int |
| `name` | string |
| `picture` | string (URL) |
| `type` | `"genre"` |

## 5. Preview & phát thử / Playback

- **Không có full stream** qua proxy này. Field `preview` là **mp3 ~30 giây** — dùng cho
  Media3 (Android) / AVPlayer (iOS) qua `expect`/`actual` (xem ADR-0010).
- URL `preview` chứa token hết hạn (param `exp=<unix>` trong query). **Không cache URL lâu** —
  lấy lại từ API khi cần phát. Có thể cache **metadata** (title, artist, cover, duration) trong
  SQLDelight, nhưng nên coi `preview` là URL ngắn hạn.

## 6. Ảnh / Images (Coil 3)

Mọi object ảnh đều có 4 size cố định — chọn theo nơi hiển thị để tiết kiệm băng thông:

| Suffix | Kích thước | Dùng cho |
| --- | --- | --- |
| `_small` | 56×56 | Avatar list, thumbnail nhỏ |
| `_medium` | 250×250 | Card, list item |
| `_big` | 500×500 | Detail header |
| `_xl` | 1000×1000 | Full-screen / now-playing |

> Field `cover` / `picture` (không suffix) trỏ tới endpoint API redirect — ưu tiên dùng bản
> có suffix để có URL ảnh trực tiếp.

## 7. Lỗi & Quota / Errors & rate limit

### 7.1. Hình dạng lỗi từ Deezer

```json
// Resource không tồn tại (vd track id sai)
{ "error": { "type": "DataException", "message": "no data", "code": 800 } }

// Endpoint không tồn tại trên proxy
{ "message": "Endpoint '/chart' does not exist" }
```

> ⚠️ Quan trọng: Deezer thường trả **HTTP 200** kèm body `error` (không phải 4xx). Vì vậy
> `safeApiCall` **không thể chỉ dựa vào HTTP status** — phải kiểm tra field `error`/`message`
> trong body để phát hiện lỗi nghiệp vụ. Map sang `AppError` xem [`../ErrorHandling.md`](../ErrorHandling.md).

| `code` (Deezer) | Ý nghĩa | Map `AppError` gợi ý |
| --- | --- | --- |
| `800` | No data (không tìm thấy) | `AppError.NotFound` |
| `4` | Quota limit (Deezer) | `AppError.Http(429, …)` |
| `100` | Item limit exceeded | `AppError.Http(400, …)` |
| `200` | Permissions | `AppError.Unauthorized` |
| `300/500/501` | Invalid token/param/service | `AppError.Http(...)` / `AppError.Unknown` |

### 7.2. Lỗi tầng RapidAPI (HTTP status thật)

| HTTP | Nguyên nhân | Ứng xử |
| --- | --- | --- |
| `401` / `403` | Key sai / thiếu / không subscribe | `AppError.Unauthorized` — kiểm tra `RAPIDAPI_KEY`. |
| `429` | Vượt quota | `AppError.Http(429)` — backoff, dừng spam. |
| `5xx` | Lỗi proxy/upstream | `AppError.Http(5xx)` — retry có backoff. |

### 7.3. Quota headers (free plan)

Mỗi response kèm header theo dõi hạn mức:

```
x-ratelimit-rapid-free-plans-hard-limit-limit:     500000
x-ratelimit-rapid-free-plans-hard-limit-remaining: 499985
x-ratelimit-rapid-free-plans-hard-limit-reset:     2589469   // giây tới khi reset
```

> Có thể đọc `...-remaining` để cảnh báo sớm khi gần hết quota.

## 8. Ví dụ cURL / Examples

```bash
BASE="https://deezerdevs-deezer.p.rapidapi.com"
H1="X-RapidAPI-Key: $RAPIDAPI_KEY"
H2="X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"

# Search (trang 2: index=25)
curl -G "$BASE/search" --data-urlencode "q=eminem" --data-urlencode "index=25" --data-urlencode "limit=25" -H "$H1" -H "$H2"

# Track / Album / Artist / Playlist
curl "$BASE/track/3135556"     -H "$H1" -H "$H2"
curl "$BASE/album/103248"      -H "$H1" -H "$H2"
curl "$BASE/artist/27"         -H "$H1" -H "$H2"
curl "$BASE/playlist/908622995" -H "$H1" -H "$H2"
```

## 9. Mapping endpoint → feature (gợi ý)

| Feature (xem [`../Features/`](../Features/)) | Endpoint dùng |
| --- | --- |
| Search | `GET /search` (phân trang index/limit) |
| Player (nghe thử) | field `preview` của Track |
| Album detail | `GET /album/{id}` (+ `tracks.data`) |
| Artist detail | `GET /artist/{id}` (lưu ý: không có top-tracks qua proxy) |
| Home / Library | `GET /playlist/{id}` cho playlist được cấu hình sẵn |

> ⚠️ **Giới hạn cần biết khi thiết kế feature:** không có chart/genre/radio/editorial và không
> có top-tracks của artist qua proxy này. Nếu UI cần các mục đó (vd màn Home "Top charts"),
> phải dùng playlist ID cố định hoặc bổ sung nguồn dữ liệu khác — ghi ADR nếu đổi hướng.
