package com.sangtq.musicappkmp.feature.albumdetail.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Album
import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class AlbumDetailUiState(
    val isLoading: Boolean = false,
    val album: Album? = null,
    val error: String? = null,
) : UiState

sealed interface AlbumDetailIntent : Intent {
    data object Retry : AlbumDetailIntent
    data class TrackClicked(val track: Track) : AlbumDetailIntent
}

sealed interface AlbumDetailEffect : Effect {
    data class OpenPlayer(val track: Track) : AlbumDetailEffect
}
