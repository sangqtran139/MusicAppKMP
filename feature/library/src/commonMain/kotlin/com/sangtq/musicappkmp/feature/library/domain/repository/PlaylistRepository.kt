package com.sangtq.musicappkmp.feature.library.domain.repository

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import kotlinx.coroutines.flow.Flow

/** Playlist tự tạo lưu local (SQLDelight). Tách khỏi [LibraryRepository] (liked/recent). */
interface PlaylistRepository {
    val playlists: Flow<List<UserPlaylist>>
    suspend fun create(name: String): Long
    suspend fun delete(id: Long)
    suspend fun addTrack(playlistId: Long, track: Track)
    suspend fun removeTrack(playlistId: Long, trackId: Long)
    fun observeName(id: Long): Flow<String?>
    fun observeTracks(id: Long): Flow<List<Track>>
}
