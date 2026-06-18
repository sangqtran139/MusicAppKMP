package com.sangtq.musicappkmp.core.playback

import kotlinx.coroutines.flow.StateFlow

/**
 * Trừu tượng playback đa nền tảng. Implementation: ExoPlayer (Android) / AVPlayer (iOS)
 * qua expect/actual trong platformModule (ADR-0010).
 */
interface AudioPlayer {
    val state: StateFlow<PlaybackState>
    fun play(track: PlayableTrack)
    fun togglePlayPause()
    fun seekTo(positionMs: Long)
    fun stop()
    fun release()
}
