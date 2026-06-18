package com.sangtq.musicappkmp.core.playback

/** Nguồn sự thật duy nhất cho playback (mini-player + now-playing cùng quan sát). */
data class PlaybackState(
    val current: PlayableTrack? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
) {
    val progress: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}
