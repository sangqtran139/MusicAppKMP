package com.sangtq.musicappkmp.catalog

import com.sangtq.musicappkmp.catalog.data.mapper.toDomain
import com.sangtq.musicappkmp.catalog.data.remote.dto.AlbumDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.ArtistDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.SearchResultDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.TrackDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CatalogMappersTest {

    @Test
    fun trackDto_mapsToDomain() {
        val dto = TrackDto(
            id = 916424,
            title = "Without Me",
            duration = 290,
            preview = "https://preview.mp3",
            explicitLyrics = true,
            artist = ArtistDto(id = 13, name = "Eminem"),
            album = AlbumDto(id = 1, title = "The Eminem Show", coverBig = "cover_big"),
        )

        val track = dto.toDomain()

        assertEquals(916424, track.id)
        assertEquals("Without Me", track.title)
        assertEquals("Eminem", track.artistName)
        assertEquals("The Eminem Show", track.albumTitle)
        assertEquals("cover_big", track.coverUrl)
        assertEquals("https://preview.mp3", track.previewUrl)
        assertEquals(290, track.durationSeconds)
        assertTrue(track.isExplicit)
    }

    @Test
    fun searchResult_setsNextIndex_whenMorePages() {
        val dto = SearchResultDto(
            data = listOf(TrackDto(id = 1, title = "A", artist = ArtistDto(id = 1, name = "X"))),
            total = 208,
            next = "https://api.deezer.com/search?index=25",
        )

        val page = dto.toDomain(currentIndex = 0, limit = 25)

        assertEquals(1, page.items.size)
        assertEquals(208, page.total)
        assertEquals(25, page.nextIndex)
    }

    @Test
    fun searchResult_nextIndexNull_whenNoNext() {
        val dto = SearchResultDto(data = emptyList(), total = 0, next = null)
        assertNull(dto.toDomain(currentIndex = 0, limit = 25).nextIndex)
    }
}
