package com.sangtq.musicappkmp.catalog.domain.model

data class Artist(
    val id: Long,
    val name: String,
    val pictureUrl: String?,
    val albumCount: Int,
    val fanCount: Int,
)
