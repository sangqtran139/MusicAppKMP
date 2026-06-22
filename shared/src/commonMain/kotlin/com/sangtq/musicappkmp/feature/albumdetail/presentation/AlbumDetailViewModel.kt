package com.sangtq.musicappkmp.feature.albumdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetAlbumUseCase
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.launch

/** Album detail: tải 1 album theo id (gồm tracks) qua [GetAlbumUseCase]. */
class AlbumDetailViewModel(
    private val albumId: Long,
    private val getAlbum: GetAlbumUseCase,
) : MviViewModel<AlbumDetailUiState, AlbumDetailIntent, AlbumDetailEffect>(AlbumDetailUiState()) {

    init {
        load()
    }

    override fun onIntent(intent: AlbumDetailIntent) = when (intent) {
        AlbumDetailIntent.Retry -> load()
        is AlbumDetailIntent.TrackClicked -> sendEffect(AlbumDetailEffect.OpenPlayer(intent.track))
    }

    private fun load() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getAlbum(albumId)) {
                is AppResult.Success -> setState { copy(isLoading = false, album = result.data) }
                is AppResult.Failure -> setState { copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
