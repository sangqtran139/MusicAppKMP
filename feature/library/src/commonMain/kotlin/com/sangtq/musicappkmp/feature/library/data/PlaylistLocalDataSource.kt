package com.sangtq.musicappkmp.feature.library.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.database.UserPlaylistTrack
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Đọc/ghi playlist tự tạo qua SQLDelight (UserPlaylist.sq). Entity → domain map tại đây. */
class PlaylistLocalDataSource(
    database: MusicDatabase,
    private val dispatchers: DispatcherProvider,
) {
    private val queries = database.userPlaylistQueries

    fun observePlaylists(): Flow<List<UserPlaylist>> =
        queries.selectPlaylistsWithMeta().asFlow().mapToList(dispatchers.io).map { rows ->
            rows.map { UserPlaylist(id = it.id, name = it.name, trackCount = it.trackCount.toInt(), coverUrl = it.coverUrl) }
        }

    fun observeName(id: Long): Flow<String?> =
        queries.selectPlaylistName(id).asFlow().mapToOneOrNull(dispatchers.io)

    fun observeTracks(id: Long): Flow<List<Track>> =
        queries.selectPlaylistTracks(id).asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toTrack() } }

    suspend fun create(name: String): Long = withContext(dispatchers.io) {
        queries.transactionWithResult {
            queries.createPlaylist(name = name, createdAt = nowMs())
            queries.lastInsertedId().executeAsOne()
        }
    }

    suspend fun delete(id: Long): Unit = withContext(dispatchers.io) {
        queries.transaction {
            queries.deleteAllTracksOf(id)
            queries.deletePlaylist(id)
        }
    }

    suspend fun addTrack(playlistId: Long, track: Track): Unit = withContext(dispatchers.io) {
        queries.addTrack(
            playlistId = playlistId,
            trackId = track.id,
            title = track.title,
            artistName = track.artistName,
            artistId = track.artistId,
            albumTitle = track.albumTitle,
            albumId = track.albumId,
            coverUrl = track.coverUrl,
            previewUrl = track.previewUrl,
            durationSeconds = track.durationSeconds.toLong(),
            isExplicit = if (track.isExplicit) 1L else 0L,
            addedAt = nowMs(),
        )
    }

    suspend fun removeTrack(playlistId: Long, trackId: Long): Unit = withContext(dispatchers.io) {
        queries.removeTrack(playlistId = playlistId, trackId = trackId)
    }
}

private fun UserPlaylistTrack.toTrack() = Track(
    id = trackId,
    title = title,
    artistName = artistName,
    artistId = artistId,
    albumTitle = albumTitle,
    albumId = albumId,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    durationSeconds = durationSeconds.toInt(),
    isExplicit = isExplicit != 0L,
)

@OptIn(ExperimentalTime::class)
private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()
