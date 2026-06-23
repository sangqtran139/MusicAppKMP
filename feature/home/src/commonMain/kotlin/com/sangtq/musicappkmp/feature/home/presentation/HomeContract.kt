package com.sangtq.musicappkmp.feature.home.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState
import com.sangtq.musicappkmp.feature.home.domain.model.FeaturedPlaylist
import com.sangtq.musicappkmp.feature.home.domain.model.HomeSection

data class HomeUiState(
    val isLoading: Boolean = false,
    val playlists: List<FeaturedPlaylist> = emptyList(),
    val sections: List<HomeSection> = emptyList(),
    val error: String? = null,
) : UiState

sealed interface HomeIntent : Intent {
    data object Load : HomeIntent
    data object Retry : HomeIntent
    data class TrackClicked(val track: Track) : HomeIntent
}

sealed interface HomeEffect : Effect {
    data class OpenPlayer(val track: Track) : HomeEffect
}
