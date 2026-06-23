package com.sangtq.musicappkmp.feature.library.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import com.sangtq.musicappkmp.feature.library.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class ObservePlaylistsUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(): Flow<List<UserPlaylist>> = repository.playlists
}

class CreatePlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(name: String): Long = repository.create(name.trim())
}

class DeletePlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}

class AddTrackToPlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long, track: Track) = repository.addTrack(playlistId, track)
}

class RemoveTrackFromPlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long, trackId: Long) = repository.removeTrack(playlistId, trackId)
}

class ObservePlaylistNameUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(id: Long): Flow<String?> = repository.observeName(id)
}

class ObservePlaylistTracksUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(id: Long): Flow<List<Track>> = repository.observeTracks(id)
}
