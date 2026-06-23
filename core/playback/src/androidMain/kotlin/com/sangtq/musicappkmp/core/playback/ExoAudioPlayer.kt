package com.sangtq.musicappkmp.core.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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

/** AudioPlayer dùng Media3 ExoPlayer. ExoPlayer phải gọi trên main thread. */
class ExoAudioPlayer(context: Context) : AudioPlayer {

    private val player = ExoPlayer.Builder(context).build()
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.update {
                    it.copy(
                        isBuffering = playbackState == Player.STATE_BUFFERING,
                        durationMs = player.duration.coerceAtLeast(0),
                    )
                }
            }
        })
        scope.launch {
            while (true) {
                _state.update {
                    it.copy(
                        positionMs = player.currentPosition.coerceAtLeast(0),
                        durationMs = player.duration.coerceAtLeast(0),
                    )
                }
                delay(POLL_MS)
            }
        }
    }

    override fun play(track: PlayableTrack) {
        _state.update { it.copy(current = track, positionMs = 0, durationMs = 0) }
        player.setMediaItem(MediaItem.fromUri(track.url))
        player.prepare()
        player.playWhenReady = true
    }

    override fun togglePlayPause() {
        player.playWhenReady = !player.playWhenReady
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun stop() {
        player.stop()
        _state.update { it.copy(isPlaying = false) }
    }

    override fun release() {
        scope.cancel()
        player.release()
    }

    private companion object {
        const val POLL_MS = 500L
    }
}
