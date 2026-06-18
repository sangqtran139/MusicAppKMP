package com.sangtq.musicappkmp.core.network

import com.sangtq.musicappkmp.core.common.AppError
import com.sangtq.musicappkmp.core.common.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * GET + map sang [AppResult], xử lý đặc thù Deezer:
 *  - HTTP lỗi (401/403/429/5xx) → [AppError.Unauthorized]/[AppError.Http].
 *  - HTTP 200 kèm body `{"error":...}` hoặc `{"message":"... does not exist"}` → lỗi nghiệp vụ.
 * Xem docs/Api/DeezerApi.md §7 và docs/ErrorHandling.md.
 */
suspend inline fun <reified T> HttpClient.deezerGet(
    json: Json,
    path: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {},
): AppResult<T> = try {
    val response = get(path) { block() }
    if (!response.status.isSuccess()) {
        val code = response.status.value
        when (code) {
            401, 403 -> AppResult.Failure(AppError.Unauthorized("API key không hợp lệ hoặc thiếu"))
            429 -> AppResult.Failure(AppError.Http(429, "Vượt giới hạn truy vấn (quota)"))
            else -> AppResult.Failure(AppError.Http(code, "Lỗi máy chủ ($code)"))
        }
    } else {
        val text = response.bodyAsText()
        val element = runCatching { json.parseToJsonElement(text) }.getOrNull()
        when {
            element is JsonObject && element.containsKey("error") ->
                AppResult.Failure(AppError.NotFound("Không tìm thấy dữ liệu"))
            element is JsonObject && element.containsKey("message") && !element.containsKey("data") && !element.containsKey("id") ->
                AppResult.Failure(AppError.Http(404, "Endpoint không khả dụng"))
            else -> AppResult.Success(json.decodeFromString<T>(text))
        }
    }
} catch (e: SerializationException) {
    AppResult.Failure(AppError.Serialization("Dữ liệu không hợp lệ"))
} catch (e: Exception) {
    AppResult.Failure(AppError.Network("Không có kết nối mạng"))
}
