package com.sangtq.musicappkmp.feature.player.presentation

import androidx.lifecycle.viewModelScope
import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.ui.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlayerViewModel(
    private val audioPlayer: AudioPlayer,
) : MviViewModel<PlayerUiState, PlayerIntent, PlayerEffect>(PlayerUiState()) {

    init {
        audioPlayer.state
            .onEach { s ->
                setState {
                    copy(
                        track = s.current,
                        isPlaying = s.isPlaying,
                        isBuffering = s.isBuffering,
                        progress = s.progress,
                        positionMs = s.positionMs,
                        durationMs = s.durationMs,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: PlayerIntent) = when (intent) {
        is PlayerIntent.Play -> audioPlayer.play(intent.track)
        PlayerIntent.TogglePlayPause -> audioPlayer.togglePlayPause()
        is PlayerIntent.SeekTo -> audioPlayer.seekTo((intent.fraction * currentState.durationMs).toLong())
    }
}
