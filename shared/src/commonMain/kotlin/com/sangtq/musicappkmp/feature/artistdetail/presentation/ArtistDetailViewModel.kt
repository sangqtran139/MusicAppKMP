package com.sangtq.musicappkmp.feature.artistdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetArtistUseCase
import com.sangtq.musicappkmp.core.common.AppResult
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.launch

/** Artist detail: tải metadata nghệ sĩ theo id qua [GetArtistUseCase]. */
class ArtistDetailViewModel(
    private val artistId: Long,
    private val getArtist: GetArtistUseCase,
) : MviViewModel<ArtistDetailUiState, ArtistDetailIntent, ArtistDetailEffect>(ArtistDetailUiState()) {

    init {
        load()
    }

    override fun onIntent(intent: ArtistDetailIntent) = when (intent) {
        ArtistDetailIntent.Retry -> load()
    }

    private fun load() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getArtist(artistId)) {
                is AppResult.Success -> setState { copy(isLoading = false, artist = result.data) }
                is AppResult.Failure -> setState { copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
