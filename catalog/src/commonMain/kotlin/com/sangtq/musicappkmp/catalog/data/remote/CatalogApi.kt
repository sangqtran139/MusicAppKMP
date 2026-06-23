package com.sangtq.musicappkmp.catalog.data.remote

import com.sangtq.musicappkmp.catalog.data.remote.dto.AlbumDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.ArtistDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.PlaylistDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.SearchResultDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.TrackDto
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.network.deezerGet
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json

/** Wrapper mỏng quanh HttpClient; trả DTO bọc trong AppResult. DTO không ra khỏi data. */
class CatalogApi(
    private val client: HttpClient,
    private val json: Json,
) {
    suspend fun search(query: String, index: Int, limit: Int): AppResult<SearchResultDto> =
        client.deezerGet(json, "search") {
            parameter("q", query)
            parameter("index", index)
            parameter("limit", limit)
        }

    suspend fun getTrack(id: Long): AppResult<TrackDto> = client.deezerGet(json, "track/$id")

    suspend fun getAlbum(id: Long): AppResult<AlbumDto> = client.deezerGet(json, "album/$id")

    suspend fun getArtist(id: Long): AppResult<ArtistDto> = client.deezerGet(json, "artist/$id")

    suspend fun getPlaylist(id: Long): AppResult<PlaylistDto> = client.deezerGet(json, "playlist/$id")
}
