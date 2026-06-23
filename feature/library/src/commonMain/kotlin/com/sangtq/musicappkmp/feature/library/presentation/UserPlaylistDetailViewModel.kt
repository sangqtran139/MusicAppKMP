package com.sangtq.musicappkmp.feature.library.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.core.ui.MviViewModel
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistNameUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.ObservePlaylistTracksUseCase
import com.sangtq.musicappkmp.feature.library.domain.usecase.RemoveTrackFromPlaylistUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserPlaylistDetailViewModel(
    private val playlistId: Long,
    observeName: ObservePlaylistNameUseCase,
    observeTracks: ObservePlaylistTracksUseCase,
    private val removeTrack: RemoveTrackFromPlaylistUseCase,
) : MviViewModel<UserPlaylistDetailUiState, UserPlaylistDetailIntent, UserPlaylistDetailEffect>(
    UserPlaylistDetailUiState(),
) {

    init {
        combine(observeName(playlistId), observeTracks(playlistId)) { name, tracks -> name to tracks }
            .onEach { (name, tracks) -> setState { copy(name = name.orEmpty(), tracks = tracks) } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: UserPlaylistDetailIntent) = when (intent) {
        is UserPlaylistDetailIntent.TrackClicked -> sendEffect(UserPlaylistDetailEffect.OpenPlayer(intent.track))
        is UserPlaylistDetailIntent.RemoveTrack -> {
            viewModelScope.launch { removeTrack(playlistId, intent.trackId) }
            Unit
        }
    }
}
