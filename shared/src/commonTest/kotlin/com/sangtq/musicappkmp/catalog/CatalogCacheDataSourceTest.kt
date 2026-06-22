package com.sangtq.musicappkmp.catalog

import app.cash.turbine.test
import com.sangtq.musicappkmp.catalog.data.local.CatalogCacheDataSource
import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.common.DispatcherProvider
import com.sangtq.musicappkmp.core.database.MusicDatabase
import com.sangtq.musicappkmp.core.database.inMemorySqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CatalogCacheDataSourceTest {

    private val dispatchers = object : DispatcherProvider {
        override val main = Dispatchers.Unconfined
        override val default = Dispatchers.Unconfined
        override val io = Dispatchers.Unconfined
    }

    private fun dataSource() =
        CatalogCacheDataSource(MusicDatabase(inMemorySqlDriver()), dispatchers)

    private fun track(id: Long) = Track(
        id = id,
        title = "Song $id",
        artistName = "Artist",
        artistId = 99,
        albumTitle = "Album",
        albumId = 1,
        coverUrl = null,
        previewUrl = "https://example.com/$id.mp3",
        durationSeconds = 30,
        isExplicit = false,
    )

    private fun album(vararg trackIds: Long) = Album(
        id = 1,
        title = "Album",
        artistName = "Artist",
        coverUrl = "cover",
        releaseDate = "2020-01-01",
        trackCount = trackIds.size,
        tracks = trackIds.map { track(it) },
    )

    @Test
    fun album_startsNull() = runTest {
        assertNull(dataSource().observeAlbum(1).first())
    }

    @Test
    fun saveAlbum_thenObserve_returnsAlbumWithTracksInOrder() = runTest {
        val ds = dataSource()
        ds.saveAlbum(album(10, 20, 30))

        ds.observeAlbum(1).test {
            val album = awaitItem()
            assertEquals("Album", album?.title)
            assertEquals(listOf(10L, 20L, 30L), album?.tracks?.map { it.id })
            assertEquals(99L, album?.tracks?.first()?.artistId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveAlbum_replacesPreviousTracks() = runTest {
        val ds = dataSource()
        ds.saveAlbum(album(10, 20, 30))
        ds.saveAlbum(album(40))
        assertEquals(listOf(40L), ds.observeAlbum(1).first()?.tracks?.map { it.id })
    }
}
