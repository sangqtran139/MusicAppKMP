package com.sangtq.musicappkmp.feature.player.presentation

import com.sangtq.musicappkmp.core.playback.PlayableTrack
import com.sangtq.musicappkmp.core.ui.Effect
import com.sangtq.musicappkmp.core.ui.Intent
import com.sangtq.musicappkmp.core.ui.UiState

data class PlayerUiState(
    val track: PlayableTrack? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val progress: Float = 0f,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
) : UiState

sealed interface PlayerIntent : Intent {
    data class Play(val track: PlayableTrack) : PlayerIntent
    data object TogglePlayPause : PlayerIntent
    data class SeekTo(val fraction: Float) : PlayerIntent
}

/** Chưa có effect one-shot cho player ở MVP. */
sealed interface PlayerEffect : Effect
