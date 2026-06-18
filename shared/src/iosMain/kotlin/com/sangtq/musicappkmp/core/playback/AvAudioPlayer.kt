package com.sangtq.musicappkmp.core.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL

/** AudioPlayer dùng AVPlayer của AVFoundation. */
@OptIn(ExperimentalForeignApi::class)
class AvAudioPlayer : AudioPlayer {

    private val player = AVPlayer()
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        scope.launch {
            while (true) {
                val durationSec = player.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
                val positionSec = CMTimeGetSeconds(player.currentTime())
                val playing = player.timeControlStatus == AVPlayerTimeControlStatusPlaying
                _state.update {
                    it.copy(
                        isPlaying = playing,
                        positionMs = (positionSec * 1000).toLong().coerceAtLeast(0),
                        durationMs = if (durationSec.isNaN()) 0 else (durationSec * 1000).toLong().coerceAtLeast(0),
                    )
                }
                delay(POLL_MS)
            }
        }
    }

    override fun play(track: PlayableTrack) {
        val url = NSURL.URLWithString(track.url) ?: return
        player.replaceCurrentItemWithPlayerItem(AVPlayerItem(uRL = url))
        player.play()
        _state.update { it.copy(current = track, isPlaying = true, positionMs = 0, durationMs = 0) }
    }

    override fun togglePlayPause() {
        if (player.timeControlStatus == AVPlayerTimeControlStatusPlaying) player.pause() else player.play()
    }

    override fun seekTo(positionMs: Long) {
        player.seekToTime(CMTimeMakeWithSeconds(positionMs / 1000.0, 1000))
    }

    override fun stop() {
        player.pause()
        _state.update { it.copy(isPlaying = false) }
    }

    override fun release() {
        scope.cancel()
        player.pause()
    }

    private companion object {
        const val POLL_MS = 500L
    }
}
