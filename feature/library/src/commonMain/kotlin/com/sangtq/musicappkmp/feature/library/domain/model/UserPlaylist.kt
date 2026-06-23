package com.sangtq.musicappkmp.feature.library.domain.model

/** Playlist tự tạo (local) — bản tóm tắt cho danh sách ở Library. */
data class UserPlaylist(
    val id: Long,
    val name: String,
    val trackCount: Int,
    val coverUrl: String?,
)
