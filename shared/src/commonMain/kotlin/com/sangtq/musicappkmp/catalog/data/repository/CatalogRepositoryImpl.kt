package com.sangtq.musicappkmp.catalog.data.repository

import com.sangtq.musicappkmp.catalog.data.local.CatalogCacheDataSource
import com.sangtq.musicappkmp.catalog.data.mapper.toDomain
import com.sangtq.musicappkmp.catalog.data.remote.CatalogApi
import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.catalog.domain.repository.CatalogRepository
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.core.common.map
import com.sangtq.musicappkmp.core.data.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CatalogRepositoryImpl(
    private val api: CatalogApi,
    private val cache: CatalogCacheDataSource,
    private val dispatchers: DispatcherProvider,
) : CatalogRepository {

    override suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchPage> =
        withContext(dispatchers.io) {
            api.search(query, index, limit).map { it.toDomain(index, limit) }
        }

    override suspend fun getTrack(id: Long): AppResult<Track> =
        withContext(dispatchers.io) { api.getTrack(id).map { it.toDomain() } }

    override fun observeAlbum(id: Long): Flow<Resource<Album>> = networkBoundResource(
        query = { cache.observeAlbum(id) },
        fetch = { api.getAlbum(id).map { dto -> cache.saveAlbum(dto.toDomain()) } },
    )

    override suspend fun getArtist(id: Long): AppResult<Artist> =
        withContext(dispatchers.io) { api.getArtist(id).map { it.toDomain() } }

    override suspend fun getPlaylist(id: Long): AppResult<Playlist> =
        withContext(dispatchers.io) { api.getPlaylist(id).map { it.toDomain() } }
}
