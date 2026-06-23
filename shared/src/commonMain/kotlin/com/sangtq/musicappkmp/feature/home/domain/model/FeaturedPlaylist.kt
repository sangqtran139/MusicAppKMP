package com.sangtq.musicappkmp.feature.home.domain.model

/** Playlist tuyển chọn hiển thị ở Home (rút gọn — chỉ cần id/title/cover cho card). */
data class FeaturedPlaylist(
    val id: Long,
    val title: String,
    val coverUrl: String?,
)
