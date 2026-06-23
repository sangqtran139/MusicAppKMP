package com.sangtq.musicappkmp.core.common

/**
 * Lỗi miền ứng dụng (không phải exception). `data` map exception/lỗi nghiệp vụ sang [AppError].
 * Bảng map từ lỗi Deezer/RapidAPI: xem `docs/Api/DeezerApi.md` và `docs/ErrorHandling.md`.
 */
sealed interface AppError {
    val message: String

    /** Mất mạng, timeout. */
    data class Network(override val message: String) : AppError

    /** Lỗi HTTP 4xx/5xx từ RapidAPI (vd 401/403 key sai, 429 quota, 5xx upstream). */
    data class Http(val code: Int, override val message: String) : AppError

    /** Không tìm thấy resource (Deezer code 800) hoặc cache rỗng. */
    data class NotFound(override val message: String) : AppError

    /** Thiếu/sai API key (401/403). */
    data class Unauthorized(override val message: String) : AppError

    /** Parse JSON lỗi. */
    data class Serialization(override val message: String) : AppError

    /** Lỗi database (SQLDelight). */
    data class Database(override val message: String) : AppError

    /** Lỗi không phân loại. */
    data class Unknown(override val message: String) : AppError
}
