package com.sangtq.musicappkmp.feature.library.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.database.LikedTrack
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.database.RecentTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Đọc/ghi thư viện cá nhân qua SQLDelight (xem core/database/Library.sq). Map row sinh ra (entity)
 * sang domain [Track] tại đây — entity không rời khỏi layer data (CLAUDE.md hard rule #4).
 */
class LibraryLocalDataSource(
    database: MusicDatabase,
    private val dispatchers: DispatcherProvider,
) {
    private val queries = database.libraryQueries

    fun observeLiked(): Flow<List<Track>> =
        queries.selectLiked().asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toTrack() } }

    fun observeRecent(): Flow<List<Track>> =
        queries.selectRecent(MAX_RECENT).asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toTrack() } }

    suspend fun toggleLike(track: Track) {
        withContext(dispatchers.io) {
            if (queries.isLiked(track.id).executeAsOne()) {
                queries.deleteLiked(track.id)
            } else {
                queries.insertLiked(
                    id = track.id,
                    title = track.title,
                    artistName = track.artistName,
                    albumTitle = track.albumTitle,
                    coverUrl = track.coverUrl,
                    previewUrl = track.previewUrl,
                    durationSeconds = track.durationSeconds.toLong(),
                    isExplicit = track.isExplicit.toLong(),
                    likedAt = nowMs(),
                )
            }
        }
    }

    suspend fun addRecent(track: Track): Unit = withContext(dispatchers.io) {
        queries.transaction {
            queries.insertRecent(
                id = track.id,
                title = track.title,
                artistName = track.artistName,
                albumTitle = track.albumTitle,
                coverUrl = track.coverUrl,
                previewUrl = track.previewUrl,
                durationSeconds = track.durationSeconds.toLong(),
                isExplicit = track.isExplicit.toLong(),
                playedAt = nowMs(),
            )
            queries.trimRecent(MAX_RECENT)
        }
    }

    private companion object {
        const val MAX_RECENT = 20L
    }
}

@OptIn(ExperimentalTime::class)
private fun nowMs(): Long = Clock.System.now().toEpochMilliseconds()

private fun LikedTrack.toTrack() = Track(
    id = id,
    title = title,
    artistName = artistName,
    albumTitle = albumTitle,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    durationSeconds = durationSeconds.toInt(),
    isExplicit = isExplicit != 0L,
)

private fun RecentTrack.toTrack() = Track(
    id = id,
    title = title,
    artistName = artistName,
    albumTitle = albumTitle,
    coverUrl = coverUrl,
    previewUrl = previewUrl,
    durationSeconds = durationSeconds.toInt(),
    isExplicit = isExplicit != 0L,
)

private fun Boolean.toLong(): Long = if (this) 1L else 0L
