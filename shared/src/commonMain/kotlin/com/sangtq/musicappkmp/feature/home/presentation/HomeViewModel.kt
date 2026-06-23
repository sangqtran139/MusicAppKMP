package com.sangtq.musicappkmp.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.ui.MviViewModel
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetFeaturedPlaylistsUseCase
import com.sangtq.musicappkmp.feature.home.domain.usecase.GetHomeSectionsUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeSections: GetHomeSectionsUseCase,
    private val getFeaturedPlaylists: GetFeaturedPlaylistsUseCase,
) : MviViewModel<HomeUiState, HomeIntent, HomeEffect>(HomeUiState()) {

    init {
        load()
    }

    override fun onIntent(intent: HomeIntent) = when (intent) {
        HomeIntent.Load, HomeIntent.Retry -> load()
        is HomeIntent.TrackClicked -> sendEffect(HomeEffect.OpenPlayer(intent.track))
    }

    private fun load() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        // Playlist tuyển chọn = best-effort (không chặn/không gây lỗi nếu rỗng).
        viewModelScope.launch {
            val playlists = getFeaturedPlaylists()
            setState { copy(playlists = playlists) }
        }
        viewModelScope.launch {
            when (val result = getHomeSections()) {
                is AppResult.Success -> setState { copy(isLoading = false, sections = result.data) }
                is AppResult.Failure -> setState { copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
