package com.sangtq.musicappkmp.feature.library.data

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import com.sangtq.musicappkmp.feature.library.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class SqlDelightPlaylistRepository(
    private val local: PlaylistLocalDataSource,
) : PlaylistRepository {

    override val playlists: Flow<List<UserPlaylist>> = local.observePlaylists()

    override suspend fun create(name: String): Long = local.create(name)
    override suspend fun delete(id: Long) = local.delete(id)
    override suspend fun addTrack(playlistId: Long, track: Track) = local.addTrack(playlistId, track)
    override suspend fun removeTrack(playlistId: Long, trackId: Long) = local.removeTrack(playlistId, trackId)
    override fun observeName(id: Long): Flow<String?> = local.observeName(id)
    override fun observeTracks(id: Long): Flow<List<Track>> = local.observeTracks(id)
}
