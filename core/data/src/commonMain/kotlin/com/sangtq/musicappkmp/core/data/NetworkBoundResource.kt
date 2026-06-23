package com.sangtq.musicappkmp.core.data

import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Offline-first single-source-of-truth: phát cache từ DB, refresh từ network, lưu lại DB, rồi
 * tiếp tục quan sát DB (xem docs/Architecture.md — "network chỉ cập nhật DB; UI quan sát DB").
 *
 * @param query   nguồn DB (Flow), `null` khi chưa có cache.
 * @param fetch   gọi network + lưu vào DB; trả [AppResult] (Unit) để biết refresh có lỗi không.
 * @param shouldFetch quyết định có refresh không dựa trên cache hiện có (mặc định: luôn refresh).
 */
fun <T> networkBoundResource(
    query: () -> Flow<T?>,
    fetch: suspend () -> AppResult<Unit>,
    shouldFetch: (T?) -> Boolean = { true },
): Flow<Resource<T>> = flow {
    val cached = query().first()
    if (shouldFetch(cached)) {
        emit(Resource.Loading(cached))
        val fetchResult = fetch()
        emitAll(
            query().map { value ->
                when {
                    value != null -> Resource.Success(value)
                    fetchResult is AppResult.Failure -> Resource.Error(fetchResult.error, null)
                    else -> Resource.Loading(null)
                }
            }
        )
    } else {
        emitAll(query().map { value -> if (value != null) Resource.Success(value) else Resource.Loading(null) })
    }
}
