package com.sangtq.musicappkmp.feature.library.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.core.ui.MviViewModel
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LibraryViewModel(
    observeLiked: ObserveLikedUseCase,
    observeRecent: ObserveRecentUseCase,
    private val toggleLike: ToggleLikeUseCase,
) : MviViewModel<LibraryUiState, LibraryIntent, LibraryEffect>(LibraryUiState()) {

    init {
        combine(observeLiked(), observeRecent()) { liked, recent -> liked to recent }
            .onEach { (liked, recent) -> setState { copy(liked = liked, recent = recent) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: LibraryIntent) = when (intent) {
        is LibraryIntent.ToggleLike -> { viewModelScope.launch { toggleLike(intent.track) }; Unit }
        is LibraryIntent.TrackClicked -> sendEffect(LibraryEffect.OpenPlayer(intent.track))
    }
}
