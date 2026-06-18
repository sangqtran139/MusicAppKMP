package com.sangtq.musicappkmp.feature.library.domain.repository

import com.sangtq.musicappkmp.catalog.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

/**
 * Thư viện cá nhân (liked + recently played). MVP lưu in-memory; bước kế tiếp thay bằng
 * SQLDelight để bền vững qua phiên (xem docs/Roadmap.md Phase 3).
 */
interface LibraryRepository {
    val liked: StateFlow<List<Track>>
    val recent: StateFlow<List<Track>>
    fun toggleLike(track: Track)
    fun addRecent(track: Track)
}
