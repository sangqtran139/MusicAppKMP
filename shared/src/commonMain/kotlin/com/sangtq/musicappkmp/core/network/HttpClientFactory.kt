package com.sangtq.musicappkmp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** JSON dùng chung — bỏ qua field thừa của Deezer (response rất nhiều field). */
fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

/**
 * HttpClient cho Deezer/RapidAPI. Auth = static header (không Bearer/refresh).
 * Engine tự chọn theo nền tảng (OkHttp Android / Darwin iOS). Xem docs/NetworkingGuide.md.
 */
fun createHttpClient(config: ApiConfig, json: Json): HttpClient = HttpClient {
    expectSuccess = false   // Deezer hay trả 200 kèm body error → tự kiểm tra

    install(ContentNegotiation) { json(json) }

    install(Logging) {
        level = LogLevel.INFO   // KHÔNG log body/headers để tránh lộ X-RapidAPI-Key
    }

    defaultRequest {
        url(config.baseUrl)
        header("X-RapidAPI-Key", config.rapidApiKey)
        header("X-RapidAPI-Host", config.rapidApiHost)
    }
}
