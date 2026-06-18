package com.sangtq.musicappkmp.core.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** AudioPlayer giả cho test (thay cho platformModule). */
class PlaybackStub : AudioPlayer {
    override val state: StateFlow<PlaybackState> = MutableStateFlow(PlaybackState())
    override fun play(track: PlayableTrack) {}
    override fun togglePlayPause() {}
    override fun seekTo(positionMs: Long) {}
    override fun stop() {}
    override fun release() {}
}
