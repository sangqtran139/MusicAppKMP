package com.sangtq.musicappkmp.feature.player

import com.sangtq.musicappkmp.core.playback.AudioPlayer
import com.sangtq.musicappkmp.core.playback.PlayableTrack
import com.sangtq.musicappkmp.core.playback.PlaybackState
import com.sangtq.musicappkmp.feature.player.presentation.PlayerIntent
import com.sangtq.musicappkmp.feature.player.presentation.PlayerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val track = PlayableTrack(1, "Without Me", "Eminem", "art", "https://p.mp3")

    @Test
    fun reflectsPlayerState() {
        val player = FakeAudioPlayer()
        val vm = PlayerViewModel(player)

        player.emit(PlaybackState(current = track, isPlaying = true, positionMs = 10, durationMs = 100))

        val state = vm.state.value
        assertEquals(track, state.track)
        assertEquals(true, state.isPlaying)
        assertEquals(0.1f, state.progress)
    }

    @Test
    fun playIntent_delegatesToPlayer() {
        val player = FakeAudioPlayer()
        PlayerViewModel(player).onIntent(PlayerIntent.Play(track))
        assertEquals(track, player.lastPlayed)
    }

    @Test
    fun seekTo_convertsFractionUsingDuration() {
        val player = FakeAudioPlayer()
        val vm = PlayerViewModel(player)
        player.emit(PlaybackState(current = track, durationMs = 200))

        vm.onIntent(PlayerIntent.SeekTo(0.5f))
        assertEquals(100L, player.lastSeekMs)
    }

    @Test
    fun togglePlayPause_delegatesToPlayer() {
        val player = FakeAudioPlayer()
        PlayerViewModel(player).onIntent(PlayerIntent.TogglePlayPause)
        assertEquals(1, player.toggleCount)
    }
}

private class FakeAudioPlayer : AudioPlayer {
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state

    var lastPlayed: PlayableTrack? = null
        private set
    var lastSeekMs: Long? = null
        private set
    var toggleCount = 0
        private set

    fun emit(value: PlaybackState) {
        _state.value = value
    }

    override fun play(track: PlayableTrack) {
        lastPlayed = track
    }

    override fun togglePlayPause() {
        toggleCount++
    }

    override fun seekTo(positionMs: Long) {
        lastSeekMs = positionMs
    }

    override fun stop() = Unit
    override fun release() = Unit
}
