package com.sangtq.musicappkmp.feature.search.presentation

import com.sangtq.musicappkmp.catalog.domain.model.Track
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val results: List<Track> = emptyList(),
    val total: Int = 0,
    val nextIndex: Int? = null,
    val error: String? = null,
) : UiState

sealed interface SearchIntent : Intent {
    data class QueryChanged(val value: String) : SearchIntent
    data object LoadMore : SearchIntent
    data object Retry : SearchIntent
    data class TrackClicked(val track: Track) : SearchIntent
}

sealed interface SearchEffect : Effect {
    data class OpenPlayer(val track: Track) : SearchEffect
}
