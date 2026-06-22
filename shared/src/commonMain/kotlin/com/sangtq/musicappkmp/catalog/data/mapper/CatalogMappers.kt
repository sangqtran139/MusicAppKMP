package com.sangtq.musicappkmp.catalog.data.mapper

import com.sangtq.musicappkmp.catalog.data.remote.dto.AlbumDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.ArtistDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.PlaylistDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.SearchResultDto
import com.sangtq.musicappkmp.catalog.data.remote.dto.TrackDto
import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.SearchPage
import com.sangtq.musicappkmp.catalog.domain.model.Track

fun TrackDto.toDomain(albumCover: String? = null): Track = Track(
    id = id,
    title = title,
    artistName = artist?.name.orEmpty(),
    albumTitle = album?.title,
    albumId = album?.id,
    coverUrl = albumCover ?: album?.coverBig ?: album?.coverMedium,
    previewUrl = preview,
    durationSeconds = duration,
    isExplicit = explicitLyrics,
)

fun ArtistDto.toDomain(): Artist = Artist(
    id = id,
    name = name,
    pictureUrl = pictureBig ?: pictureMedium ?: pictureXl,
    albumCount = nbAlbum,
    fanCount = nbFan,
)

fun AlbumDto.toDomain(): Album {
    val cover = coverBig ?: coverMedium ?: coverXl
    return Album(
        id = id,
        title = title,
        artistName = artist?.name.orEmpty(),
        coverUrl = cover,
        releaseDate = releaseDate,
        trackCount = nbTracks,
        tracks = tracks?.data.orEmpty().map { it.toDomain(albumCover = cover) },
    )
}

fun PlaylistDto.toDomain(): Playlist = Playlist(
    id = id,
    title = title,
    description = description,
    coverUrl = pictureBig ?: pictureMedium ?: pictureXl,
    trackCount = nbTracks,
    tracks = tracks?.data.orEmpty().map { it.toDomain() },
)

fun SearchResultDto.toDomain(currentIndex: Int, limit: Int): SearchPage = SearchPage(
    items = data.map { it.toDomain() },
    total = total,
    nextIndex = if (next != null && data.isNotEmpty()) currentIndex + limit else null,
)
