# NetworkingGuide

Toàn bộ networking đi qua **Ktor Client** + **kotlinx.serialization**, cấu hình tập trung ở
`core:network`. Quyết định và lý do: [ADR-0006](ADR/0006-ktor-networking.md).

Backend nội dung là **Deezer** truy cập qua proxy **RapidAPI** (`deezerdevs-deezer`).
Hợp đồng API đầy đủ (endpoint, model, error, quota): [`Api/DeezerApi.md`](Api/DeezerApi.md).

> ⚠️ Deezer ở proxy này là API **ẩn danh**: auth bằng **static header**, **không** có
> login/logout/refresh token. Mọi mô tả về Bearer/OAuth bên dưới (nếu có) chỉ áp dụng khi app
> bổ sung backend riêng cho tính năng người dùng — không thuộc catalog Deezer.

## API Layer

```
RemoteDataSource (feature/data/remote)
   │ dùng
   ▼
HttpClient (core:network, singleton qua Koin)
   │ engine theo nền tảng
   ├─ androidMain: OkHttp engine
   └─ iosMain:     Darwin engine
```

- `RemoteDataSource`/`*Api` là wrapper mỏng quanh `HttpClient`, trả `Dto`.
- RepositoryImpl gọi `*Api`, map `Dto → Model`, trả `AppResult`. **`Dto` không ra khỏi `data`.**

```kotlin
class CatalogApi(private val client: HttpClient) {
    suspend fun getTrack(id: Long): TrackDto = client.get("track/$id").body()

    suspend fun search(query: String, index: Int = 0, limit: Int = 25): SearchResultDto =
        client.get("search") {
            parameter("q", query)
            parameter("index", index)   // phân trang: tăng index thêm limit mỗi trang
            parameter("limit", limit)
        }.body()
}
```

> Phân trang `/search` dùng `index` + `limit` (không dùng field `next` của Deezer vì nó trỏ về
> host gốc). Advanced search filter **không** hoạt động qua proxy. Xem [`Api/DeezerApi.md §2.1–2.2`](Api/DeezerApi.md#21-phân-trang--pagination-chỉ-áp-dụng-cho-search).

## HttpClient factory (core:network)

Auth = **static RapidAPI header** gắn vào mọi request qua `defaultRequest`. Không có plugin
`Auth`/bearer, không có refresh.

```kotlin
fun createHttpClient(
    engine: HttpClientEngine,
    config: ApiConfig,   // chứa rapidApiKey + host, nạp từ build config (xem mục dưới)
    json: Json,
): HttpClient = HttpClient(engine) {
    expectSuccess = false   // Deezer hay trả HTTP 200 kèm body "error" -> tự kiểm tra body

    install(ContentNegotiation) { json(json) }

    install(Logging) {
        level = LogLevel.HEADERS          // KHÔNG log body; KHÔNG log header X-RapidAPI-Key
        logger = KermitKtorLogger()
        sanitizeHeader { it == "X-RapidAPI-Key" }   // ẩn key khỏi log
    }

    defaultRequest {
        url("https://deezerdevs-deezer.p.rapidapi.com/")
        header("X-RapidAPI-Key", config.rapidApiKey)
        header("X-RapidAPI-Host", "deezerdevs-deezer.p.rapidapi.com")
        contentType(ContentType.Application.Json)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }
}
```

## API Key handling (bắt buộc)

Hard rule #10: **không secret trong code/log/fixture.**

1. Đặt key ở `local.properties` (đã `.gitignore`):
   ```properties
   RAPIDAPI_KEY=xxxxxxxxxxxxxxxxxxxx
   ```
2. Đọc trong Gradle và truyền vào build config (BuildConfig field / BuildKonfig / resource sinh ra),
   rồi map vào `ApiConfig` cung cấp qua Koin. **Không** hardcode key trong `commonMain`.
3. Logging: ẩn header `X-RapidAPI-Key` (`sanitizeHeader`), không log URL có credential.
4. CI/CD: nạp key qua biến môi trường/secret, không in ra log build.

> ℹ️ Deezer proxy không phân biệt người dùng → key là **app-level secret** (1 key cho cả app),
> không phải token theo phiên đăng nhập. Vì thế **không** lưu ở `SecureStorage` per-user; nó là
> cấu hình build. (`SecureStorage` dành cho token người dùng nếu sau này có backend auth riêng.)

## Request Lifecycle

```
RepositoryImpl.getTrack(id)
   │ withContext(dispatchers.io)
   ▼
CatalogApi.getTrack(id)  → HttpClient.get("track/$id")
   │ defaultRequest gắn sẵn X-RapidAPI-Key / X-RapidAPI-Host
   ▼
ContentNegotiation parse JSON → TrackDto
   │ kiểm tra field "error"/"message" trong body (Deezer trả 200 + error)
   ▼
RepositoryImpl: TrackDto.toDomain()  +  cache metadata vào TrackDao (offline-first)
   │
trả AppResult.Success(Track)   ── hoặc ── AppResult.Failure(AppError) nếu lỗi
```

> ⚠️ Không cache **URL `preview`** lâu dài (có token hết hạn) — chỉ cache metadata. Xem
> [`Api/DeezerApi.md §5`](Api/DeezerApi.md#5-preview--phát-thử).

## Error Handling (networking)

`data` bắt exception của Ktor **và** kiểm tra body lỗi của Deezer, rồi map sang `AppError`
(chi tiết: [`ErrorHandling.md`](ErrorHandling.md)):

```kotlin
suspend inline fun <reified T> safeApiCall(block: suspend () -> HttpResponse): AppResult<T> = try {
    val res = block()
    // Deezer thường trả 200 kèm {"error":{...}} hoặc {"message":"... does not exist"}
    val raw = res.bodyAsText()
    DeezerError.parse(raw)?.let { return AppResult.Failure(it.toAppError()) }
    AppResult.Success(json.decodeFromString<T>(raw))
} catch (e: ClientRequestException) {            // 401/403/429 từ RapidAPI
    AppResult.Failure(AppError.Http(e.response.status.value, e.message))
} catch (e: ServerResponseException) {           // 5xx
    AppResult.Failure(AppError.Http(e.response.status.value, e.message))
} catch (e: IOException) {                        // mất mạng, timeout
    AppResult.Failure(AppError.Network("Không có kết nối mạng"))
} catch (e: SerializationException) {
    AppResult.Failure(AppError.Serialization("Dữ liệu không hợp lệ"))
}
```

- Layer trên `data` **không** bắt exception của Ktor — chỉ xử lý `AppResult.Failure`.
- Không log key `X-RapidAPI-Key`, header Authorization, hay URL có credential.
- Quota: đọc header `x-ratelimit-rapid-free-plans-hard-limit-remaining` để cảnh báo sớm; `429`
  → backoff. Xem [`Api/DeezerApi.md §7`](Api/DeezerApi.md#7-lỗi--quota--errors--rate-limit).
