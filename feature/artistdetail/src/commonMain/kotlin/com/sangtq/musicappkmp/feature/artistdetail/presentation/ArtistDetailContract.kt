package com.sangtq.musicappkmp.feature.artistdetail.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Artist
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class ArtistDetailUiState(
    val isLoading: Boolean = false,
    val artist: Artist? = null,
    val error: String? = null,
) : UiState

sealed interface ArtistDetailIntent : Intent {
    data object Retry : ArtistDetailIntent
}

/** Artist detail không có side effect (proxy không cho top-tracks — chỉ metadata). */
sealed interface ArtistDetailEffect : Effect
