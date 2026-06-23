package com.sangtq.musicappkmp.feature.albumdetail.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.catalog.domain.usecase.GetAlbumUseCase
import com.sangtq.musicappkmp.core.common.Resource
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Album detail offline-first: quan sát [GetAlbumUseCase] (cache + refresh). Hiển thị cache ngay,
 * cập nhật khi refresh xong; nếu refresh lỗi mà vẫn có cache thì giữ cache (xem networkBoundResource).
 */
class AlbumDetailViewModel(
    private val albumId: Long,
    private val getAlbum: GetAlbumUseCase,
) : MviViewModel<AlbumDetailUiState, AlbumDetailIntent, AlbumDetailEffect>(AlbumDetailUiState()) {

    private var observeJob: Job? = null

    init {
        observe()
    }

    override fun onIntent(intent: AlbumDetailIntent) = when (intent) {
        AlbumDetailIntent.Retry -> observe()
        is AlbumDetailIntent.TrackClicked -> sendEffect(AlbumDetailEffect.OpenPlayer(intent.track))
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = getAlbum(albumId)
            .onEach { resource ->
                setState {
                    copy(
                        isLoading = resource is Resource.Loading,
                        album = resource.data,
                        error = (resource as? Resource.Error)?.error?.message,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
