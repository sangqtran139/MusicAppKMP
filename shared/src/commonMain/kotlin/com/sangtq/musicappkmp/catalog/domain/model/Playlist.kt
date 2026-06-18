package com.sangtq.musicappkmp.catalog.domain.model

data class Playlist(
    val id: Long,
    val title: String,
    val description: String?,
    val coverUrl: String?,
    val trackCount: Int,
    val tracks: List<Track>,
)
