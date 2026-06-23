package com.sangtq.musicappkmp.feature.artistdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetArtistUseCase
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** Artist detail offline-first: quan sát [GetArtistUseCase] (cache + refresh metadata nghệ sĩ). */
class ArtistDetailViewModel(
    private val artistId: Long,
    private val getArtist: GetArtistUseCase,
) : MviViewModel<ArtistDetailUiState, ArtistDetailIntent, ArtistDetailEffect>(ArtistDetailUiState()) {

    private var observeJob: Job? = null

    init {
        observe()
    }

    override fun onIntent(intent: ArtistDetailIntent) = when (intent) {
        ArtistDetailIntent.Retry -> observe()
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = getArtist(artistId)
            .onEach { resource ->
                setState {
                    copy(
                        isLoading = resource is Resource.Loading,
                        artist = resource.data,
                        error = (resource as? Resource.Error)?.error?.message,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
