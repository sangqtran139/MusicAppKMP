package com.sangtq.musicappkmp.catalog.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.database.CachedAlbum
import com.sangtq.musicappkmp.core.database.CachedAlbumTrack
import com.sangtq.musicappkmp.core.database.MusicDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

/**
 * Cache catalog trong SQLDelight (single source of truth). Map entity sinh ra → domain tại đây;
 * entity không rời khỏi layer data (CLAUDE.md hard rule #4).
 */
class CatalogCacheDataSource(
    database: MusicDatabase,
    private val dispatchers: DispatcherProvider,
) {
    private val queries = database.catalogCacheQueries

    fun observeAlbum(id: Long): Flow<Album?> = combine(
        queries.selectAlbum(id).asFlow().mapToOneOrNull(dispatchers.io),
        queries.selectAlbumTracks(id).asFlow().mapToList(dispatchers.io),
    ) { album, tracks ->
        album?.toDomain(tracks.map { it.toTrack() })
    }

    suspend fun saveAlbum(album: Album): Unit = withContext(dispatchers.io) {
        queries.transaction {
            queries.upsertAlbum(
                id = album.id,
                title = album.title,
                artistName = album.artistName,
                coverUrl = album.coverUrl,
                releaseDate = album.releaseDate,
                trackCount = album.trackCount.toLong(),
            )
            queries.deleteAlbumTracks(album.id)
            album.tracks.forEachIndexed { index, track ->
                queries.insertAlbumTrack(
                    albumId = album.id,
                    position = index.toLong(),
                    id = track.id,
                    title = track.title,
                    artistName = track.artistName,
                    artistId = track.artistId,
                    albumTitle = track.albumTitle,
                    trackAlbumId = track.albumId,
                    coverUrl = track.coverUrl,
                    previewUrl = track.previewUrl,
                    durationSeconds = track.durationSeconds.toLong(),
                    isExplicit = if (track.isExplicit) 1L else 0L,
                )
            }
        }
    }
}

private fun CachedAlbum.toDomain(tracks: List<Track>) = Album(
    id = id,
    title = title,
    artistName = artistName,
    coverUrl = coverUrl,
    releaseDate = releaseDate,
    trackCount = trackCount.toInt(),
    tracks = tracks,
)

private fun CachedAlbumTrack.toTrack() = Track(
    id = id,
    title = title,
    artistName = artistName,
    artistId = artistId,
    albumTitle = albumTitle,
    albumId = trackAlbumId,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    durationSeconds = durationSeconds.toInt(),
    isExplicit = isExplicit != 0L,
)
