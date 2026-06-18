package com.sangtq.musicappkmp.feature.library.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class LibraryUiState(
    val liked: List<Track> = emptyList(),
    val recent: List<Track> = emptyList(),
) : UiState {
    fun isLiked(id: Long): Boolean = liked.any { it.id == id }
}

sealed interface LibraryIntent : Intent {
    data class ToggleLike(val track: Track) : LibraryIntent
    data class TrackClicked(val track: Track) : LibraryIntent
}

sealed interface LibraryEffect : Effect {
    data class OpenPlayer(val track: Track) : LibraryEffect
}
