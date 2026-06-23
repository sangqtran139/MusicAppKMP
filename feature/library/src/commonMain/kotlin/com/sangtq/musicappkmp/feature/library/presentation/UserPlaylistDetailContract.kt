package com.sangtq.musicappkmp.feature.library.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class UserPlaylistDetailUiState(
    val name: String = "",
    val tracks: List<Track> = emptyList(),
) : UiState

sealed interface UserPlaylistDetailIntent : Intent {
    data class TrackClicked(val track: Track) : UserPlaylistDetailIntent
    data class RemoveTrack(val trackId: Long) : UserPlaylistDetailIntent
}

sealed interface UserPlaylistDetailEffect : Effect {
    data class OpenPlayer(val track: Track) : UserPlaylistDetailEffect
}
