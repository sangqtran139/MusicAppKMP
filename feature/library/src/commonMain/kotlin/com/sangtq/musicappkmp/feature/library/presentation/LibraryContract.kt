package com.sangtq.musicappkmp.feature.library.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState
import com.sangtq.musicappkmp.feature.library.domain.model.UserPlaylist

data class LibraryUiState(
    val liked: List<Track> = emptyList(),
    val recent: List<Track> = emptyList(),
    val playlists: List<UserPlaylist> = emptyList(),
    val showCreateDialog: Boolean = false,
    /** Khi != null: hiện dialog "thêm vào playlist" cho track này. */
    val addTarget: Track? = null,
) : UiState {
    fun isLiked(id: Long): Boolean = liked.any { it.id == id }
}

sealed interface LibraryIntent : Intent {
    data class ToggleLike(val track: Track) : LibraryIntent
    data class TrackClicked(val track: Track) : LibraryIntent
    data class PlaylistClicked(val id: Long) : LibraryIntent
    data object NewPlaylistClicked : LibraryIntent
    data class CreatePlaylist(val name: String) : LibraryIntent
    data class AddToPlaylistClicked(val track: Track) : LibraryIntent
    data class AddToPlaylist(val playlistId: Long) : LibraryIntent
    data object DismissDialog : LibraryIntent
}

sealed interface LibraryEffect : Effect {
    data class OpenPlayer(val track: Track) : LibraryEffect
    data class OpenPlaylist(val id: Long) : LibraryEffect
}
