package com.sangtq.musicappkmp.core.network

import com.sangtq.musicappkmp.core.config.BuildKonfig

/** Cấu hình API tầng app (không phải token per-user). Key nạp từ build (local.properties). */
data class ApiConfig(
    val baseUrl: String = "https://deezerdevs-deezer.p.rapidapi.com/",
    val rapidApiHost: String = "deezerdevs-deezer.p.rapidapi.com",
    val rapidApiKey: String = BuildKonfig.RAPIDAPI_KEY,
)
