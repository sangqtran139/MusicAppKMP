package com.sangtq.musicappkmp.core.common

/**
 * Kết quả offline-first: [data] là giá trị cache hiện có (có thể null khi chưa có cache).
 * - [Loading]: đang refresh từ network, kèm cache cũ nếu có.
 * - [Success]: có dữ liệu (từ cache hoặc vừa refresh).
 * - [Error]: refresh lỗi; vẫn kèm cache cũ nếu có (offline vẫn xem được).
 *
 * Dùng cho luồng đọc single-source-of-truth (xem core/data networkBoundResource, docs/Architecture.md).
 */
sealed interface Resource<out T> {
    val data: T?

    data class Loading<out T>(override val data: T?) : Resource<T>
    data class Success<out T>(override val data: T) : Resource<T>
    data class Error<out T>(val error: AppError, override val data: T?) : Resource<T>
}
