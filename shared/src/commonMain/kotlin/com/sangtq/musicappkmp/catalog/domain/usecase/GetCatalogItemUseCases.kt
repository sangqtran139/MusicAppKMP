package com.sangtq.musicappkmp.catalog.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import kotlinx.coroutines.flow.Flow

class GetTrackUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Track> = repository.getTrack(id)
}

class GetAlbumUseCase(private val repository: CatalogRepository) {
    /** Album offline-first (cache + refresh) — xem networkBoundResource. */
    operator fun invoke(id: Long): Flow<Resource<Album>> = repository.observeAlbum(id)
}

class GetArtistUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Artist> = repository.getArtist(id)
}

class GetPlaylistUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Playlist> = repository.getPlaylist(id)
}
