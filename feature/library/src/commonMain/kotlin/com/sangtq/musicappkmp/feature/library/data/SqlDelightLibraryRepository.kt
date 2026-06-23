package com.sangtq.musicappkmp.feature.library.data

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Thư viện cá nhân bền vững qua SQLDelight (thay [InMemoryLibraryRepository] cũ — docs/Roadmap.md
 * Phase 3/4). Chỉ delegate sang [LibraryLocalDataSource]; không giữ state in-memory.
 */
class SqlDelightLibraryRepository(
    private val local: LibraryLocalDataSource,
) : LibraryRepository {

    override val liked: Flow<List<Track>> = local.observeLiked()
    override val recent: Flow<List<Track>> = local.observeRecent()

    override suspend fun toggleLike(track: Track) = local.toggleLike(track)
    override suspend fun addRecent(track: Track) = local.addRecent(track)
}
