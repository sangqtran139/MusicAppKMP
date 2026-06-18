package com.sangtq.musicappkmp.catalog.domain.model

data class Album(
    val id: Long,
    val title: String,
    val artistName: String,
    val coverUrl: String?,
    val releaseDate: String?,
    val trackCount: Int,
    val tracks: List<Track>,
)
