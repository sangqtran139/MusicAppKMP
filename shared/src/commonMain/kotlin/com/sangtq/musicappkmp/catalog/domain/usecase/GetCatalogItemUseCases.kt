package com.sangtq.musicappkmp.catalog.domain.usecase

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppResult

class GetTrackUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Track> = repository.getTrack(id)
}

class GetAlbumUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Album> = repository.getAlbum(id)
}

class GetArtistUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Artist> = repository.getArtist(id)
}

class GetPlaylistUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(id: Long): AppResult<Playlist> = repository.getPlaylist(id)
}
