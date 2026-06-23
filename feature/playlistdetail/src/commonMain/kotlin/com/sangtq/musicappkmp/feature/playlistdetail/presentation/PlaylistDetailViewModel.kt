package com.sangtq.musicappkmp.feature.playlistdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetPlaylistUseCase
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Playlist detail offline-first: quan sát [GetPlaylistUseCase] (cache + refresh). Hiển thị cache
 * ngay, cập nhật khi refresh xong; lỗi mà còn cache thì giữ cache (xem networkBoundResource).
 */
class PlaylistDetailViewModel(
    private val playlistId: Long,
    private val getPlaylist: GetPlaylistUseCase,
) : MviViewModel<PlaylistDetailUiState, PlaylistDetailIntent, PlaylistDetailEffect>(PlaylistDetailUiState()) {

    private var observeJob: Job? = null

    init {
        observe()
    }

    override fun onIntent(intent: PlaylistDetailIntent) = when (intent) {
        PlaylistDetailIntent.Retry -> observe()
        is PlaylistDetailIntent.TrackClicked -> sendEffect(PlaylistDetailEffect.OpenPlayer(intent.track))
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = getPlaylist(playlistId)
            .onEach { resource ->
                setState {
                    copy(
                        isLoading = resource is Resource.Loading,
                        playlist = resource.data,
                        error = (resource as? Resource.Error)?.error?.message,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
