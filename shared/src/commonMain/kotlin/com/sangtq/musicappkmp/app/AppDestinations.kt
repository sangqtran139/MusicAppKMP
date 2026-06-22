package com.sangtq.musicappkmp.app

import kotlinx.serialization.Serializable

/**
 * Route type-safe cho NavHost (ADR-0008). NavHost + lắp ráp graph nằm ở [MainScaffold] (`:shared`);
 * điều hướng giữa màn dùng các route này hoặc callback có kiểu.
 */
@Serializable
data object HomeRoute

@Serializable
data object ExploreRoute

@Serializable
data object LibraryRoute

@Serializable
data class AlbumRoute(val albumId: Long)

@Serializable
data class ArtistRoute(val artistId: Long)

@Serializable
data class PlaylistRoute(val playlistId: Long)
