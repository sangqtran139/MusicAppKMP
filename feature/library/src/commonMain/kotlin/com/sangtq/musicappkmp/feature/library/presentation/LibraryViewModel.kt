package com.sangtq.musicappkmp.feature.library.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.core.ui.MviViewModel
import com.sangtq.musicappkmp.feature.library.domain.usecase.AddTrackToPlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.CreatePlaylistUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveLikedUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistsUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObserveRecentUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LibraryViewModel(
    observeLiked: ObserveLikedUseCase,
    observeRecent: ObserveRecentUseCase,
    observePlaylists: ObservePlaylistsUseCase,
    private val toggleLike: ToggleLikeUseCase,
    private val createPlaylist: CreatePlaylistUseCase,
    private val addToPlaylist: AddTrackToPlaylistUseCase,
) : MviViewModel<LibraryUiState, LibraryIntent, LibraryEffect>(LibraryUiState()) {

    init {
        combine(observeLiked(), observeRecent(), observePlaylists()) { liked, recent, playlists ->
            Triple(liked, recent, playlists)
        }
            .onEach { (liked, recent, playlists) ->
                setState { copy(liked = liked, recent = recent, playlists = playlists) }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: LibraryIntent) = when (intent) {
        is LibraryIntent.ToggleLike -> launch { toggleLike(intent.track) }
        is LibraryIntent.TrackClicked -> sendEffect(LibraryEffect.OpenPlayer(intent.track))
        is LibraryIntent.PlaylistClicked -> sendEffect(LibraryEffect.OpenPlaylist(intent.id))
        LibraryIntent.NewPlaylistClicked -> setState { copy(showCreateDialog = true) }
        is LibraryIntent.CreatePlaylist -> {
            val name = intent.name.trim().ifBlank { "My Playlist" }
            launch { createPlaylist(name) }
            setState { copy(showCreateDialog = false) }
        }
        is LibraryIntent.AddToPlaylistClicked -> setState { copy(addTarget = intent.track) }
        is LibraryIntent.AddToPlaylist -> {
            currentState.addTarget?.let { track -> launch { addToPlaylist(intent.playlistId, track) } }
            setState { copy(addTarget = null) }
        }
        LibraryIntent.DismissDialog -> setState { copy(showCreateDialog = false, addTarget = null) }
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
