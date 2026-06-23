package com.sangtq.musicappkmp.catalog.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO Deezer — chỉ khai báo field dùng tới; field thừa bị Json.ignoreUnknownKeys bỏ qua. */

@Serializable
data class TrackDto(
    val id: Long,
    val title: String = "",
    val duration: Int = 0,
    val preview: String? = null,
    @SerialName("explicit_lyrics") val explicitLyrics: Boolean = false,
    @SerialName("md5_image") val md5Image: String? = null,
    val artist: ArtistDto? = null,
    val album: AlbumDto? = null,
)

@Serializable
data class ArtistDto(
    val id: Long,
    val name: String = "",
    @SerialName("picture_medium") val pictureMedium: String? = null,
    @SerialName("picture_big") val pictureBig: String? = null,
    @SerialName("picture_xl") val pictureXl: String? = null,
    @SerialName("nb_album") val nbAlbum: Int = 0,
    @SerialName("nb_fan") val nbFan: Int = 0,
)

@Serializable
data class AlbumDto(
    val id: Long,
    val title: String = "",
    @SerialName("cover_medium") val coverMedium: String? = null,
    @SerialName("cover_big") val coverBig: String? = null,
    @SerialName("cover_xl") val coverXl: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("nb_tracks") val nbTracks: Int = 0,
    val artist: ArtistDto? = null,
    val tracks: TrackListDto? = null,
)

@Serializable
data class PlaylistDto(
    val id: Long,
    val title: String = "",
    val description: String? = null,
    @SerialName("picture_medium") val pictureMedium: String? = null,
    @SerialName("picture_big") val pictureBig: String? = null,
    @SerialName("picture_xl") val pictureXl: String? = null,
    @SerialName("nb_tracks") val nbTracks: Int = 0,
    val tracks: TrackListDto? = null,
)

@Serializable
data class TrackListDto(
    val data: List<TrackDto> = emptyList(),
)

@Serializable
data class SearchResultDto(
    val data: List<TrackDto> = emptyList(),
    val total: Int = 0,
    val next: String? = null,
)
