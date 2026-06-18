package com.sangtq.musicappkmp.catalog.domain.repository

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.AppResult

/** Cổng truy cập catalog Deezer (domain interface, implement ở data). */
interface CatalogRepository {
    suspend fun search(query: String, index: Int, limit: Int = 25): AppResult<SearchPage>
    suspend fun getTrack(id: Long): AppResult<Track>
    suspend fun getAlbum(id: Long): AppResult<Album>
    suspend fun getArtist(id: Long): AppResult<Artist>
    suspend fun getPlaylist(id: Long): AppResult<Playlist>
}
