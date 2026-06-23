package com.sangtq.musicappkmp.catalog.domain.repository

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.Resource
import kotlinx.coroutines.flow.Flow

/** Cổng truy cập catalog Deezer (domain interface, implement ở data). */
interface CatalogRepository {
    suspend fun search(query: String, index: Int, limit: Int = 25): AppResult<SearchPage>
    suspend fun getTrack(id: Long): AppResult<Track>
    /** Detail offline-first: quan sát cache (SQLDelight) + refresh network (xem networkBoundResource). */
    fun observeAlbum(id: Long): Flow<Resource<Album>>
    fun observeArtist(id: Long): Flow<Resource<Artist>>
    fun observePlaylist(id: Long): Flow<Resource<Playlist>>
}
