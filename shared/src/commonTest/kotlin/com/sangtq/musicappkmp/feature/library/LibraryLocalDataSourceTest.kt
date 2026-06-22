package com.sangtq.musicappkmp.feature.library

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.database.inMemorySqlDriver
import com.sangtq.musicappkmp.feature.library.data.LibraryLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LibraryLocalDataSourceTest {

    private val dispatchers = object : DispatcherProvider {
        override val main = Dispatchers.Unconfined
        override val default = Dispatchers.Unconfined
        override val io = Dispatchers.Unconfined
    }

    private fun dataSource() =
        LibraryLocalDataSource(MusicDatabase(inMemorySqlDriver()), dispatchers)

    private fun track(id: Long) = Track(
        id = id,
        title = "Song $id",
        artistName = "Artist",
        albumTitle = null,
        coverUrl = null,
        previewUrl = "https://example.com/$id.mp3",
        durationSeconds = 30,
        isExplicit = false,
    )

    @Test
    fun liked_startsEmpty() = runTest {
        assertTrue(dataSource().observeLiked().first().isEmpty())
    }

    @Test
    fun toggleLike_addsThenRemoves() = runTest {
        val ds = dataSource()
        ds.toggleLike(track(1))
        assertEquals(listOf(1L), ds.observeLiked().first().map { it.id })

        ds.toggleLike(track(1))
        assertTrue(ds.observeLiked().first().isEmpty())
    }

    @Test
    fun addRecent_cappedAtMax() = runTest {
        val ds = dataSource()
        repeat(25) { ds.addRecent(track(it.toLong())) }
        assertEquals(20, ds.observeRecent().first().size)
    }

    @Test
    fun addRecent_sameTrackNotDuplicated() = runTest {
        val ds = dataSource()
        ds.addRecent(track(7))
        ds.addRecent(track(7))
        assertEquals(listOf(7L), ds.observeRecent().first().map { it.id })
    }
}
