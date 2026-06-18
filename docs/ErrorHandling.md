# ErrorHandling

Nguyên tắc: **không ném exception xuyên layer.** Layer `data` bắt mọi exception và trả
`AppResult.Failure(AppError)`. Layer trên chỉ xử lý `AppResult`.

> ⚠️ **Đặc thù Deezer (qua RapidAPI):** API hay trả **HTTP 200 kèm body lỗi**
> (`{"error":{"code":800,...}}` hoặc `{"message":"Endpoint ... does not exist"}`). Vì vậy `data`
> **không** chỉ dựa vào HTTP status mà phải **kiểm tra body** để phát hiện lỗi nghiệp vụ. Bảng
> map `code` Deezer → `AppError` và lỗi tầng RapidAPI (401/403/429): [`Api/DeezerApi.md §7`](Api/DeezerApi.md#7-lỗi--quota--errors--rate-limit).

## Error Categories

`AppError` (định nghĩa ở `core:common`) là `sealed interface`:

```kotlin
sealed interface AppError {
    val message: String
    data class Network(override val message: String) : AppError          // mất mạng, timeout
    data class Http(val code: Int, override val message: String) : AppError  // 4xx/5xx
    data class NotFound(override val message: String) : AppError          // không tìm thấy resource
    data class Unauthorized(override val message: String) : AppError      // 401/403 RapidAPI: key sai/thiếu
    data class Serialization(override val message: String) : AppError     // parse lỗi
    data class Database(override val message: String) : AppError          // lỗi SQLDelight
    data class Unknown(override val message: String) : AppError
}

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}
```

| Category | Nguồn | Ứng xử mặc định |
| --- | --- | --- |
| `Network` | `IOException`, timeout | Cho phép retry; hiển thị "Kiểm tra kết nối". |
| `Http(4xx)` | `ClientRequestException` / `429` quota | Không retry tự động; `429` → backoff. |
| `Http(5xx)` | `ServerResponseException` | Retry có backoff; "Hệ thống đang bận". |
| `Unauthorized` | `401/403` RapidAPI (key sai/thiếu) | Báo cấu hình sai; kiểm tra `RAPIDAPI_KEY`. |
| `NotFound` | Deezer `code 800` / DB rỗng | Hiển thị empty state. |
| `Serialization` | `SerializationException` | Không retry; log để dev xử lý schema. |
| `Database` | lỗi SQLDelight | Fallback network nếu được; log. |
| `Unknown` | còn lại | Thông báo chung; log. |

## Logging Strategy

- Dùng **Kermit**, tag theo class. Không `println`.
- Log lỗi ở **điểm bắt** (trong `data`), kèm context (endpoint, mã lỗi) nhưng **không** kèm token/PII.
- Mức log: `e()` cho lỗi thật, `w()` cho tình huống hồi phục được, `d()` chỉ ở debug build.

```kotlin
catch (e: ServerResponseException) {
    Logger.e(tag = "CatalogApi") { "getTrack failed: ${e.response.status}" }
    AppResult.Failure(AppError.Http(e.response.status.value, "Hệ thống đang bận"))
}
```

## Retry Strategy

- Retry chỉ cho lỗi **tạm thời**: `Network` và `Http(5xx)`.
- Dùng **exponential backoff** có giới hạn (vd 3 lần: 1s, 2s, 4s) ở layer `data`/`core:data`.
- Không retry: `Http(4xx)` (trừ 401→refresh do plugin Auth lo), `Serialization`, `NotFound`.
- User-triggered retry: màn hình có nút "Thử lại" phát `Intent.Retry` (xem [`StateManagement.md`](StateManagement.md)).

```kotlin
suspend fun <T> retrying(times: Int = 3, block: suspend () -> AppResult<T>): AppResult<T> {
    var delayMs = 1_000L
    repeat(times - 1) {
        val r = block()
        val retriable = r is AppResult.Failure &&
            (r.error is AppError.Network || (r.error is AppError.Http && r.error.code >= 500))
        if (!retriable) return r
        delay(delayMs); delayMs *= 2
    }
    return block()
}
```

## User Facing Error Handling

- `AppError` được map sang **chuỗi localizable** ở layer `presentation` (qua Compose Resources), không lưu text trong domain.
- ViewModel đặt `error` vào `UiState` (hiển thị inline) và/hoặc phát `Effect.ShowError` (snackbar one-shot).
- Empty state, error state, loading state đều là **field của `UiState`** — UI render thuần theo state.

```kotlin
private fun mapError(error: AppError): String = when (error) {
    is AppError.Network      -> getString(Res.string.error_network)
    is AppError.Unauthorized -> getString(Res.string.error_session_expired)
    is AppError.Http         -> getString(Res.string.error_server)
    else                     -> getString(Res.string.error_generic)
}
```
