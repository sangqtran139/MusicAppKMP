package com.sangtq.musicappkmp.feature.library.domain.repository

import com.sangtq.musicappkmp.catalog.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Thư viện cá nhân (liked + recently played) lưu bền vững local qua SQLDelight
 * (xem docs/Roadmap.md Phase 3/4, docs/ADR/0007).
 */
interface LibraryRepository {
    val liked: Flow<List<Track>>
    val recent: Flow<List<Track>>
    suspend fun toggleLike(track: Track)
    suspend fun addRecent(track: Track)
}
