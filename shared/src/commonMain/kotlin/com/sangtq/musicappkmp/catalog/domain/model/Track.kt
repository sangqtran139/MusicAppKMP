package com.sangtq.musicappkmp.catalog.domain.model

/** Bài hát (domain). `previewUrl` là mp3 ~30s của Deezer (xem docs/Api/DeezerApi.md §5). */
data class Track(
    val id: Long,
    val title: String,
    val artistName: String,
    val albumTitle: String?,
    val albumId: Long?,
    val coverUrl: String?,
    val previewUrl: String?,
    val durationSeconds: Int,
    val isExplicit: Boolean,
)
