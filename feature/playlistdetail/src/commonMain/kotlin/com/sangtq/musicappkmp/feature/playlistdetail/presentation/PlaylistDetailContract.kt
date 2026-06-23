package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Playlist
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class PlaylistDetailUiState(
    val isLoading: Boolean = false,
    val playlist: Playlist? = null,
    val error: String? = null,
) : UiState

sealed interface PlaylistDetailIntent : Intent {
    data object Retry : PlaylistDetailIntent
    data class TrackClicked(val track: Track) : PlaylistDetailIntent
}

sealed interface PlaylistDetailEffect : Effect {
    data class OpenPlayer(val track: Track) : PlaylistDetailEffect
}
