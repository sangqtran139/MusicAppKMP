package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetPlaylistUseCase
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.launch

/** Playlist detail: tải 1 playlist theo id (gồm tracks) qua [GetPlaylistUseCase]. */
class PlaylistDetailViewModel(
    private val playlistId: Long,
    private val getPlaylist: GetPlaylistUseCase,
) : MviViewModel<PlaylistDetailUiState, PlaylistDetailIntent, PlaylistDetailEffect>(PlaylistDetailUiState()) {

    init {
        load()
    }

    override fun onIntent(intent: PlaylistDetailIntent) = when (intent) {
        PlaylistDetailIntent.Retry -> load()
        is PlaylistDetailIntent.TrackClicked -> sendEffect(PlaylistDetailEffect.OpenPlayer(intent.track))
    }

    private fun load() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getPlaylist(playlistId)) {
                is AppResult.Success -> setState { copy(isLoading = false, playlist = result.data) }
                is AppResult.Failure -> setState { copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
